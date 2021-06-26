package com.example.shieldher1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView userNameTextView;
    TextView passwordTextView;
    TextView confirmPasswordTextView;
    Button registerButton;
    TextView loginTextView;
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameTextView = findViewById(R.id.userNameRegisterTextView);
        passwordTextView = findViewById(R.id.passwordRegisterTextView);
        confirmPasswordTextView = findViewById(R.id.confirmPasswordRegisterTextView);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        db = new DataBaseHelper(this);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userNameTextView.getText().toString().trim();
                String pass = passwordTextView.getText().toString().trim();
                String confPass = confirmPasswordTextView.getText().toString().trim();

                if(pass.equals(confPass)){
                    Boolean  checkEmail = db.checkEmail(user);
                    if(checkEmail == true){
                        long val = db.addUser(user, pass);
                        if(val > 0){
                            if(isValid(user)) {
                                Intent moveToLogin = new Intent(RegisterActivity.this, LogInActivity.class);
                                startActivity(moveToLogin);
                                Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Check email(include Proper domain name)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9-]+(?:\\."+
                "[a-zA-Z0-9-]+)*@" +
                "(?:[a-zA-Z0-9-]{4,6}+\\.)+[a-z" +
                "A-Z]{2,3}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
