package com.example.michaelgifford.cisc434project2;

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



import java.net.URISyntaxException;

public class LOGINREGISTER extends ActionBarActivity {

    private EditText mUsernameView;
    private String mUsername;
    private EditText mPasswordView;
    private String mPassword;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.4/");
            mSocket.connect();
        } catch (URISyntaxException e) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);

        // Login Form Setup
        mUsernameView = (EditText) findViewById(R.id.usernameField);
        mPasswordView = (EditText) findViewById(R.id.passwordField);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mSocket.on("login", onLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("login", onLogin);
    }

    // Attempts to login with supplied user/pw. If form errors such as wrong user, missing field, etc. exist then errors presented and no login attempted
    private void attemptLogin() {
        // Reset Errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        // Check that username is valid
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(("Invalid Username"));
            mUsernameView.requestFocus();
            return;
        }
        // Check that password is valid
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Invalid Password");
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;
        mPassword = password;

        // Define input user info as JSON object
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("username", mUsername);
            userInfo.put("password", mPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Attempt login
        mSocket.emit("login", userInfo);

        // Added for testing
        //Intent intent = new Intent(LOGINREGISTER.this, LOBBY.class); //change "LOBBY" to lobby activity class name
        //startActivity(intent);
    }

    // Attempts to register a new usre with supplied user/pw. If form errors such as wrong user, missing field, etc. exist then errors presented and no login attempted
    private void attemptRegister() {
        // Reset Errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        // Check that username is valid
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(("Invalid Username"));
            mUsernameView.requestFocus();
            return;
        }
        // Check that password is valid
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Invalid Password");
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;
        mPassword = password;

        // Define input user info as JSON object
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("username", mUsername);
            userInfo.put("password", mPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Attempt to register
        mSocket.emit("register", userInfo);

        // Added for testing
        // Intent intent = new Intent(LOGINREGISTER.this, LOBBY.class); //change "LOBBY" to lobby activity class name
       // startActivity(intent);
    }

    // Login (Go to lobby activity) if server response to login attempt is TRUE
    private Emitter.Listener onLogin = new Emitter.Listener() {

       //mSocket.emit();

        @Override
        public void call(Object... args) {
            //JSONObject data = (JSONObject) args[0];
            boolean serverResponse = (Boolean) args[0];

            /*
            try {
                //serverResponse = data;
            } catch (JSONException e) {
                return;
            }
            */


            // Check if server accepted login/register attempt
           if(serverResponse == true) {
               Intent intent = new Intent(LOGINREGISTER.this, LOBBY.class); //change "LOBBY" to lobby activity class name
               startActivity(intent);
           } else {
                // Stay on login/register screen
                // Show invalid login/register error
               mUsernameView.setError(("Invalid Attempt. Try Again!"));
               mUsernameView.requestFocus();
               return;
           }
            finish();
        }
    };
}
