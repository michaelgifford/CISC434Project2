var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var bcrypt = require('bcrypt');
var SALT_FACTOR = 10;

// user table schema
var userSchema = new Schema({
	name: { type: String, required: true, index: { unique: true } },
	password: { type: String, required: true },
	connection: { type: String }
});

// message table schema
var messageSchema = new Schema({
	room: { type: Schema.Types.ObjectId, ref: 'Room' },
	sender: { type: Schema.Types.ObjectId, ref: 'User' },
	receiver: [String],
	content: { type: String, required: true },
	datetime: { type: Date, default: Date.now }
});

// room table schema
var roomSchema = new Schema({
	key: { type: String, required: true, index: { unique: true } },
	name: { type: String, required: true },
	admin: { type: String, required: true },
	online: [String]
});

// before inserting record hash password
userSchema.pre('save', function(next) {
	var user = this;

	// check if password has changed
	if (!user.isModified('password')) return next();

	// generate salt for password
	bcrypt.genSalt(SALT_FACTOR, function(err, salt) {
		if (err) return next(err);
		// hash password with salt and store
		bcrypt.hash(user.password, salt, function(err, hash) {
			if (err) return next(err);
			user.password = hash;
			next();
		});
	});
});

userSchema.methods.comparePassword = function(candidatePassword, cb) {
	bcrypt.compare(candidatePassword, this.password, function(err, isMatch) {
		if (err) return console.log(err);
		cb(null, isMatch)
	});
};

// export schemas
var User = mongoose.model('User', userSchema);
var Message = mongoose.model('Message', messageSchema);
var Room = mongoose.model('Room', roomSchema);
module.exports.User = User;
module.exports.Message = Message;
module.exports.Room = Room;
