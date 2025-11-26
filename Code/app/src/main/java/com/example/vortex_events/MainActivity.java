package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);




        DatabaseWorker worker = new DatabaseWorker();
        Date enroll_start = new Date();
        RegisteredUser user = new RegisteredUser(MainActivity.this, "7805551234",
                "russelwestbrook@washed.com", "Russel Westbrook", 0,0);

//
//        DatabaseWorker worker = new DatabaseWorker();
//        Date enroll_start = new Date();
//        RegisteredUser user = new RegisteredUser(MainActivity.this, "7805551234",
//                "russelwestbrook@washed.com", "Russel Westbrook", "-0-0");
//
//        Event event = new Event("Washed", "Albania", "Russel Westbrook", "123456789",
//                enroll_start, enroll_start, enroll_start, enroll_start, null, null, 5 );
//
//        worker.createEvent(user, event).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d("Event", "Success");
//                    } else {
//                        Log.d("Event","Failure");
//                    }
//                }
//        );
//



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Add to every activity
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //Add the rest of the activities when finished
            //made a boolean function to implement highlighting items. will implement later
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home){
                    return true;
                }else if(itemId == R.id.nav_create) {
                    Intent intent = new Intent(getApplicationContext(), CreateActivityEvents.class);
                    startActivity(intent);
                    return true;
                }else if(itemId == R.id.nav_explore){
                    Intent intent = new Intent(getApplicationContext(), ExplorePage.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(getApplicationContext(), SearchEvents.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(getApplicationContext(), SearchEvents.class);
                    startActivity(intent);
                    return true;
                }else if (itemId == R.id.nav_scan_qr) {
                    Intent intent = new Intent(getApplicationContext(), QRCodeScanner.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });


    }


}