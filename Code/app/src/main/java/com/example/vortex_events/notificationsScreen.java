package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class
notificationsScreen extends AppCompatActivity {

    ListView notifcations;
    String userID;
    DatabaseWorker dbWorker;
    ArrayList<AppNotification> items;
    notificationsAdapter adapter;



    String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notifications_screen);


        userID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        dbWorker = new DatabaseWorker();
        notifcations = findViewById(R.id.notification_list);
        items = new ArrayList<>();
        adapter = new notificationsAdapter(notificationsScreen.this, items);
        notifcations.setAdapter(adapter);
        dbWorker.getUserByDeviceID(userID).addOnCompleteListener(new OnCompleteListener<RegisteredUser>() {
            @Override
            public void onComplete(@NonNull Task<RegisteredUser> task) {
                if (task.isSuccessful()){
                    RegisteredUser user = task.getResult();
                    Log.d("Noti", user.notifications.toString());


                    for (String notification: user.notifications ){
                        dbWorker.getNotificationByID(notification).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        AppNotification currwentNot = document.toObject(AppNotification.class);
                                        items.add(currwentNot);
                                        adapter.notifyDataSetChanged();
                                        Log.d("Noti", currwentNot.title);
                                    }else{
                                        Log.d("DOC ERROR", "Doc doesnt exits");
                                    }
                                }else{
                                    Log.d("DOC EXISTENCE", "DOC DOEST EXIST");
                                }
                            }
                        });
                    }


                }
            }
        });








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






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}