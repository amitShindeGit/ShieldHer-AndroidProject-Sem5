package com.example.shieldher1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    TextView userNameLoginTextView;
    TextView passwordLoginTextView;
    Button loginButton;
    TextView registerTextView;

    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if(isLoggedIn){
            Intent homeIntent = new Intent(LogInActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }

        userNameLoginTextView = findViewById(R.id.userNameLoginTextView);
        passwordLoginTextView = findViewById(R.id.passwordLoginTextView);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        db = new DataBaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userNameLoginTextView.getText().toString().trim();
                String pass = passwordLoginTextView.getText().toString().trim();
                Boolean res = db.checkUser(user, pass);

                if(res == true){
                    Intent homeIntent = new Intent(LogInActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    SharedPreferences.Editor editor = getSharedPreferences("loginDetail", MODE_PRIVATE).edit();
                    editor.putString("user", user);
                    editor.putString("pass", pass);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    finish();
                }
                else {
                    Toast.makeText(LogInActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
