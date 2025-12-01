package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

    RegisteredUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        @SuppressLint("HardwareIds")
        String currentDeviceID = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        DatabaseWorker databaseWorker = new DatabaseWorker();

//        databaseWorker.getUserByDeviceID(currentDeviceID).addOnSuccessListener(user -> {
//            if (user != null) {
//                Log.d("MainActivity", "User found: " + user.getDeviceID());
//                this.user = user;
//            } else {
//                Log.d("MainActivity", "User not found");
//            }
//        }).addOnFailureListener(e -> {
//            Log.e("MainActivity", "Error getting user", e);
//            });
//
//        TextView title = findViewById(R.id.mainTitle);
//        title.setText(String.format("Hello, %s!", user.getName()));
//
//        ImageView profile_icon = findViewById(R.id.profile_icon);
//        ImageView notification_icon = findViewById(R.id.notifications_icon);
//
//        profile_icon.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), Profile.class);
//            intent.putExtra("prev_activity", "main");
//            startActivity(intent);
//        });

//        TODO: need after notifications are implemented
//        notification_icon.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), Notifications.class);
//            startActivity(intent);
//        });





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