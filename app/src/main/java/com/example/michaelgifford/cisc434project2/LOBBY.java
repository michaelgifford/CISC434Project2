package com.example.michaelgifford.cisc434project2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URISyntaxException;
import java.util.ArrayList;

import com.example.michaelgifford.cisc434project2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import io.socket.emitter.Emitter;
//import io.socket.client.IO;
//import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.widget.ListView;
import android.widget.ListAdapter;

public class LOBBY extends ActionBarActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.4/");
            mSocket.connect();
        } catch (URISyntaxException e) {
        }
    }

    //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.ListView,StringArray);

    // Pull room data from server into roomNameArray
    String[] roomNameArray = {"Room 1", "Room 2", "Room 3", "Room 4", "Room 5", "Room 6", "Room 7", "Room 8", "Room 9", "Room 10", "Room 11", "Room 12", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        /*
        ListAdapter adapter = new ArrayAdapter<String>(
                this,
                R.layout.customrow,
                roomNameArray
        );
        ListView roomList = (ListView) findViewById(R.id.roomList);
        roomList.setAdapter(adapter);
        */
        //////////////////


        ArrayList<roomResults> roomResults = GetRoomResults();

        final ListView roomList = (ListView) findViewById(R.id.roomList);
        roomList.setAdapter(new MyCustomBaseAdapter(this, roomResults));


        roomList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = roomList.getItemAtPosition(position);
                roomResults fullObject = (roomResults) o;
                Toast.makeText(LOBBY.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
            }
        });

        mSocket.on("lobby", onLoad);
    }

    private ArrayList<roomResults> GetRoomResults(){
        ArrayList<roomResults> results = new ArrayList<roomResults>();

        // Server response as below:
        // key, name, admin, online (array of user names)

        roomResults sr = new roomResults();
        sr = new roomResults();
        //roomResults sr = new onLoad();
        addRoomData();
        results.add(sr);

        sr = new roomResults();
        sr.setName("Jane Doe");
        sr.setNumCurrentUsers("702-555-1234");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Lauren Sherman");
        sr.setNumCurrentUsers("415-555-1234");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Fred Jones");
        sr.setNumCurrentUsers("612-555-8214");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Bill Withers");
        sr.setNumCurrentUsers("424-555-8214");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Donald Fagen");
        sr.setNumCurrentUsers("424-555-1234");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Steve Rude");
        sr.setNumCurrentUsers("515-555-2222");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Roland Bloom");
        sr.setNumCurrentUsers("978-555-1111");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Sandy Baguskas");
        sr.setNumCurrentUsers("978-555-2222");
        results.add(sr);

        sr = new roomResults();
        sr.setName("Scott Taylor");
        sr.setNumCurrentUsers("512-555-2222");
        results.add(sr);

        return results;
    }

    private void addRoomData(String roomKey, String roomName, String roomAdmin, String[] roomOnlineUsers) {
        roomResults sr = new roomResults();
        sr.setName(roomName);
        String numCurrentUsers = Integer.toString(roomOnlineUsers.length);
        sr.setNumCurrentUsers(numCurrentUsers);

    }

    private Emitter.Listener onLoad = new Emitter.Listener() {
        //mSocket.emit();

        @Override
        public void call(Object... args) {
            // key, name, admin, online (array of user names)
            String roomKey = (String) args[0];
            String roomName = (String) args[1];
            String roomAdmin = (String) args[2];
            String[] roomOnlineUsers = (String []) args[3];

            addRoomData(roomKey, roomName, roomAdmin, roomOnlineUsers);


            finish();
        }
    };



}
