package com.example.shieldher1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    Button button;
    ArrayAdapter arrayAdapter;
    ArrayList arrayList;
    EditText editText;
    ArrayList arrayList1;

    Button sendButton;
    TextView smsText;

    //REQUEST CODE
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    //Storing Informations
    SharedPreferences sharedPreferences;

    //Current Location
    FusedLocationProviderClient fusedLocationProviderClient;

    //Button aNIMATION
    Animation scale_up, scale_down;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Btm Nav Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.addNumberButton);
        arrayList = new ArrayList<String>();
        arrayList1 = new ArrayList<String>();
        editText = findViewById(R.id.enterNumberEditText);

        sendButton = findViewById(R.id.sendButton);
        smsText = findViewById(R.id.smsText);
        smsText.setVisibility(View.INVISIBLE);

        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        //Initializing SharedPreference
        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.shieldher1", Context.MODE_PRIVATE);

        //Getting previous data,if available
        final HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("nums", null);
        if(set == null){
            arrayList.add("NO CONTACTS");
            Toast.makeText(this, "Please add some numbers.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Nope", Toast.LENGTH_SHORT).show();
            arrayList = new ArrayList(set);
        }

        //  Log.i("SET", set.toString());

        //Contacts
        Intent intent = getIntent();
        String number = intent.getStringExtra("numbers");
        if(number != null){
            if(arrayList.contains("NO CONTACTS")){
                arrayList.remove(0);
            }
            arrayList.add(number);

            HashSet<String> set1 = new HashSet<>(arrayList);
            sharedPreferences.edit().putStringSet("nums", set1).apply();
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, arrayList);
        listView.setAdapter(arrayAdapter);

        if(arrayAdapter.getCount() == 0){
            arrayList.add("NO CONTACTS");
        }


        //Deleting Numberes
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(!arrayList.get(0).equals("NO CONTACTS")) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Delete Number")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    arrayList.remove(position);
                                    HashSet<String> set = new HashSet<>(arrayList);
                                    sharedPreferences.edit().putStringSet("nums", set).apply();

                                    if (arrayList.isEmpty()) {
                                        arrayList.add("NO CONTACTS");
                                    }
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                return false;
            }
        });

        //SMS PART
        if(checkPermission(Manifest.permission.SEND_SMS)){
//            button.setEnabled(true);
            Toast.makeText(this, "Send SMS READY", Toast.LENGTH_SHORT).show();

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        //Current Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Location Permission
        if(ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }else{
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //Button Animation

        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    onSend(v);
                    sendButton.startAnimation(scale_up);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendButton.startAnimation(scale_down);
                }
                return true;
            }
        });

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addItems(v);
                    button.startAnimation(scale_up);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    button.startAnimation(scale_down);
                }
                return true;
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //BottomNavigation Bar
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.nav_contacts:
                            Intent intent2 = new Intent(HomeActivity.this, ContactActivity.class);
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            finish();

                            break;

                        case R.id.nav_map:
                            Intent intent3 = new Intent(HomeActivity.this, LocationActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            finish();

                            break;

                        case R.id.nav_info:
                            Intent intent4 = new Intent(HomeActivity.this, InfoActivity.class);
                            startActivity(intent4);
                            overridePendingTransition(0, 0);
                            finish();

                            break;
                    }

                    return true;
                }
            };

    //Add Numbers And Store in SharedPreferences
    public void addItems(View view){
        if(editText.getText().length() == 10) {
            // arrayList.add(editText.getText().toString());
            arrayList1.add(editText.getText().toString());

            if(arrayList1.size() > 0){
                if (arrayList.contains(editText.getText().toString())) {
                    Toast.makeText(this, "Same no. exists", Toast.LENGTH_SHORT).show();
                }else {
                    if(arrayList.contains("NO CONTACTS")){
                        arrayList.remove(0);
                    }
                    arrayList.add(editText.getText().toString());
                    editText.setText("");
                    Toast.makeText(this, "Number Added Successfully", Toast.LENGTH_SHORT).show();
                    button.animate().rotation(360).setDuration(200);
                    HashSet<String> set = new HashSet<>(arrayList);
                    sharedPreferences.edit().putStringSet("nums", set).apply();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }else {
            Toast.makeText(this, "Number Length is Wrong", Toast.LENGTH_SHORT).show();
        }

    }


    //SMS Permission
    public boolean checkPermission(String permission){

        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    //Getting Current Location
    private void getLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){

                    try {
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        smsText.setText("I am here :\n" + addresses.get(0).getAddressLine(0));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //Send Button(Sending Message)
    public void onSend(View view){
        ArrayList<String> phoneNUmber = new ArrayList<String>();
        for(int i=0; i<arrayList.size(); i++) {
            phoneNUmber.add(listView.getItemAtPosition(i).toString());
        }
        String message = smsText.getText().toString();

        for(int j=0; j<arrayList.size(); j++) {

            if (phoneNUmber.get(0).equals("NO CONTACTS") || phoneNUmber.get(j) == null || phoneNUmber.get(j).length() == 0 || message == null || message.length() == 0) {

                Toast.makeText(this, "No numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkPermission(Manifest.permission.SEND_SMS)) {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNUmber.get(j), null, message, null, null);
                sendButton.animate().rotationYBy(360).setDuration(2000);
                Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissio denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
