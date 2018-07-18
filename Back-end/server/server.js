var app = require('express')();
var http = require('http').Server(app);
var session = require('express-session');
var bodyParser = require('body-parser');
var io = require('socket.io')(http);
var fs = require('fs');
var mongoose = require('mongoose');
var mongoStore = require('connect-mongo')(session);
var User = require(__dirname + '/models.js').User;
var Message = require(__dirname + '/models.js').Message;
var Room = require(__dirname + '/models.js').Room;

// ssl
sslkey = fs.readFileSync('ssl-key.pem');
sslcert = fs.readFileSync('ssl-cert.pem');

var ssloptions = {
	key: sslkey,
	cert: sslcert
};

var https = require('https').Server(ssloptions, app);

// connect to database
mongoose.connect('mongodb://localhost/chat');
var db = mongoose.connection;

// check connection to database
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function(callback) {
	console.log('db is connected');
})

// initialize session
var sessionMiddleware = session({
	secret: 'LydepHAt0aNzqERMX9wq',
	resave: true,
	saveUninitialized: true,
	store: new mongoStore({ mongooseConnection: db }),
	// user data
	login: false,
	cookie: { maxAge: 60000*15 }
});

// use session
app.use(sessionMiddleware);
io.use(function(socket, next) {
	sessionMiddleware(socket.request, socket.request.res, next);
});

// use parser for request body
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// permission validation
function requireLogin(req, res, next) {
	if(!req.session.login) {
		res.redirect('/');			
		return next(new Error("Permission denied."));
	}
	next();
}

// root
app.get('/', function(req, res) {
	res.sendFile(__dirname + '/index.html');
});

// lobby
app.get('/lobby', requireLogin, function(req, res) {
	res.sendFile(__dirname + '/lobby.html');
});

// chat room
app.get(/^\/room\/([a-z0-9]{6})$/, requireLogin, function(req, res) {
	req.session.roomkey = req.params[0];
	req.session.save(function(err) { if (err) console.log(err); });
	res.sendFile(__dirname + '/room.html');
});

// IO //
// socket connection interface
io.on('connection', function(socket) {
	// session variables
	var sess = socket.request.session;

	console.log('[connection] ' + socket.id);
	// user connection status
	if (sess.username) { 
		console.log('user connected: ' + sess.username + ' ' + socket.id);
		User.update({ name: sess.username }, { connection: socket.id }, function(err) { if (err) console.log(err); });
		if (sess.roomkey) socket.join(sess.roomkey);
	}
	socket.on('disconnect', function() { 
		if (sess.username) {
			console.log('user disconnected: ' + sess.username + ' ' + socket.id);
			User.update({ name: sess.username }, { connection: null }, function(err) { if (err) console.log(err); });
		}
	});

	// user registration
	socket.on('register', function(data) {
		var newUser = new User({ name: data.username, password: data.password });
		newUser.save(function(err) {
			if (err) {
				console.log(err.message);
				socket.emit('register', false);
			} else { 
				sess.login = true;
				sess.username = data.username;
				User.update({ name: sess.username }, { connection: socket.id }, function(err) { if (err) console.log(err); });
				console.log('user created: ' + sess.username);
				sess.save(function(err) { if (err) console.log(err); });
				socket.emit('register', true);
			}
		});
	});

	// user login
	socket.on('login', function(data) {
		User.findOne({ name: data.username }).exec(function(err, user) {
			if (err) console.log(err);
			user.comparePassword(data.password, function(err, isMatch) {
				if (err) console.log(err);
				else if (isMatch) {
					sess.login = true;
					sess.username = data.username;
					User.update({ name: sess.username }, { connection: socket.id }, function(err) { if (err) console.log(err); });
					console.log('login success: ' + sess.username);
					sess.save(function(err) { if (err) console.log(err); });
					socket.emit('login', true);
				} else {
				console.log('login failed: ' + data.username);
				socket.emit('login', false);
				}
			});
		});
	});
	
	// user logout
	socket.on('logout', function() {
		sess.destroy(function(err) { 
			if (err) { 
				console.log(err); 
				socket.emit('logout', false);
			} else {
				socket.emit('logout', true);
			}
		});
	});

	// get list of rooms for lobby
	socket.on('lobby', function() {
		Room.find().exec().then(function(rooms) {
			rooms.forEach(function(room) {
				var data = { key: room.key, name: room.name, admin: room.admin, online: room.online };
				console.log('lobby data sent: ' + data.name);
				socket.emit('lobby', data);
			});
		});
	});

	// create room event
	socket.on('build', function(roomName) {
		console.log(roomName)
		// generate unique identifier key for room
		var code = (Math.random() + 1).toString(16).substring(2, 8);
		// store new room
		var newRoom = new Room({ key: code, name: roomName, admin: sess.username });
		newRoom.save(function(err) {
			if (err) console.log(err);
			console.log('room created: ' + roomName);
			socket.emit('build', true);
		});
	});

	// delete room event
	socket.on('destroy', function(key) {
		Room.findOne({ key: key, admin: sess.username }).exec(function(err, room) {
			Message.remove({ room: room._id }, function(err) { if (err) console.log(err); });
			Room.remove(room, function(err) { if (err) console.log(err); });
			console.log('room removed: ' + room.name);
		});
	});

	// join room event
	socket.on('join', function(key) {
		console.log('[join]');
		sess.roomkey = key;
		sess.save(function(err) { if (err) console.log(err); });
		socket.join(key);
		User.update({ name: sess.username }, { connection: socket.id }, function(err) { if (err) console.log(err); });
		Room.findOne({ key: sess.roomkey }).exec(function(err, room) {
			if (err) console.log(err);
			// update users online in room
			var list = room.online;
			list.push(sess.username);
			Room.update({ _id: room._id }, { online: list }, function(err) { if (err) console.log(err); });
			// get previous messages	
			Message.find({ room: room._id, receiver: [] }).populate('sender').sort({ datetime: 'asc' }).exec(function(err, messages) {
				if (err) console.log(err);
				messages.forEach(function(msg) {
					var data = { sender: msg.sender.name, content: msg.content, datetime: msg.datetime };
					//socket.emit('message', data);
					socket.emit('message', data);
				});
			});
		});
	});

	// leave room event
	socket.on('leave', function() {
		Room.findOne({ key: sess.roomkey }).exec(function(err, room) {
			if (err) console.log(err);
			console.log('leave: ' + room.name);
			var list = room.online;
			console.log('list: ' + list);
			var index = list.indexOf(sess.username);
			if (index !== -1) list.splice(index, 1);
			if (list.length <= 0) list = [];
			console.log('list: ' + list);
			Room.update({ room: room._id }, { online: list }, function(err) { if (err) console.log(err); });
			socket.leave(sess.roomkey);
			sess.roomkey = null;
			sess.save(function(err) { if (err) console.log(err); });
		});	
	});

	// chat message event
	socket.on('message', function(msg) {
		User.update({ name: sess.username }, { connection: socket.id }, function(err) { if (err) console.log(err); });
		var direct = [];
		msg.replace(/@(\w+)(?=\s)/g, function(all, capture) { direct.push(capture); });
		var data = { sender: sess.username, content: msg, datetime: new Date().toISOString() };
		console.log('[new message] ' + data.sender + ': ' + data.content);
		// find room message was sent in
		Room.findOne({ key: sess.roomkey }).exec(function(err, room) {
			if (err) console.log(err);
			// find user message was sent by
			User.findOne({ name: sess.username }).exec(function(err, user) {
				if (err) console.log(err);
				var newMessage = new Message({ room: room._id, sender: user._id, receiver: direct, content: data.content, datetime: data.datetime });
				// save new message
				newMessage.save(function(err) {
					if (err) console.log(err);
					// broadcast message
					if (direct.length > 0) {
						direct.push(sess.username);
						User.find({ name: direct }).exec(function(err, user) {
							if (err) console.log(err);
							user.forEach(function(u) {
								console.log(u.name);
								io.to(u.connection).emit('message', data);
							});
						});	
					} else {
					//socket.emit('message', data);
					io.to(sess.roomkey).emit('message', data);
					}
				});
			});
		});
	});
	
});

http.listen(3000);
https.listen(3003);
console.log('listening on ports 3000 (http) & 3003 (https)');
