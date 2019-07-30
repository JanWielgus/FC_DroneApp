package com.example.fcdroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText passwordText;
    Button logInButton;
    String password = "haslo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordText = (EditText) findViewById(R.id.passwordText);
        logInButton = (Button) findViewById(R.id.logInButton);


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (passwordText.getText().toString().compareTo(password) == 0)
                {
                    // successful login - return to the main activity
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    finish(); // return to the main activity
                }
                /*
                else
                {
                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }
}
