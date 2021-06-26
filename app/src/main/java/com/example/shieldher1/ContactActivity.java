package com.example.shieldher1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    ArrayList<String> arrayList;
    ListView listView;
    ArrayAdapter arrayAdapter;
    String number;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        //contactTextView = findViewById(R.id.contactTextView);
        arrayList = new ArrayList<String>();
        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        else{
            getContacts();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(ContactActivity.this)
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Add Number")
                        .setMessage("Do you want to add\n " + listView.getItemAtPosition(position) + " \nto your Emergency contacts?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ContactActivity.this, HomeActivity.class);
                                intent.putExtra("numbers", listView.getItemAtPosition(position).toString().substring(listView.getItemAtPosition(position).toString().lastIndexOf("-") + 1));
                                Toast.makeText(ContactActivity.this, listView.getItemAtPosition(position).toString() + " added successfully", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getContacts();
            }

        }
    }

    private void getContacts() {

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            arrayList.add(name + "-"+ "\n" + number);
            //contactTextView.setText(arrayList.toString());
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            Intent intent1 = new Intent(ContactActivity.this, HomeActivity.class);
                            startActivity(intent1);
                            overridePendingTransition(0, 0);
                            finish();

                            break;

                        case R.id.nav_map:
                            Intent intent3 = new Intent(ContactActivity.this, LocationActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            finish();

                            break;

                        case R.id.nav_info:
                            Intent intent4 = new Intent(ContactActivity.this, InfoActivity.class);
                            startActivity(intent4);
                            overridePendingTransition(0, 0);
                            finish();

                            break;
                    }

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
