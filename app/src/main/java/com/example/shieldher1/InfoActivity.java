package com.example.shieldher1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //BTM Nav Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }

    //BTM nav
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            Intent intent1 = new Intent(InfoActivity.this, HomeActivity.class);
                            startActivity(intent1);
                            overridePendingTransition(0, 0);
                            finish();
                            break;

                        case R.id.nav_contacts:
                            Intent intent3 = new Intent(InfoActivity.this, ContactActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            finish();
                            break;

                        case R.id.nav_map:
                            Intent intent4 = new Intent(InfoActivity.this, LocationActivity.class);
                            startActivity(intent4);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                    }

                    return true;
                }
            };

    //Log Out Button
    public void LogOut(View view){
        SharedPreferences.Editor editor = getSharedPreferences("loginDetail", MODE_PRIVATE).edit();
        editor.putString("user", "");
        editor.putString("pass", "");
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        intent.putExtra("finish", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    //Close App on clicking Back Button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
