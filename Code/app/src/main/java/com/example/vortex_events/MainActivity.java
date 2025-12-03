package com.example.vortex_events;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPastEvents;
    private ConstraintLayout layoutEmptyState;
    private PastEventAdapter pastEventAdapter;
    private List<Event> pastEventList = new ArrayList<>();
    private DatabaseWorker databaseWorker;
    private TextView homeTitleTextView;
    private Button exploreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        homeTitleTextView = findViewById(R.id.textView_Home_title);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        databaseWorker = new DatabaseWorker();

        recyclerViewPastEvents = findViewById(R.id.recyclerView_past_events);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        exploreButton = findViewById(R.id.btnExplore);

        pastEventAdapter = new PastEventAdapter(pastEventList, this);
        recyclerViewPastEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPastEvents.setAdapter(pastEventAdapter);

        ImageButton profileButton = findViewById(R.id.imageView_profile);
        ImageButton notificationsButton = findViewById(R.id.imageView_notifications);

        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("prev_activity", "home");
                startActivity(intent);
            });
        }

        if (exploreButton != null) {
            exploreButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ExplorePage.class);
                startActivity(intent);
            });
        }

        if (notificationsButton != null) {
            notificationsButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), notificationsScreen.class);
                startActivity(intent);
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }


        loadSignedUpEventsForCurrentUser();

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
    @SuppressLint("HardwareIds")
    private void loadSignedUpEventsForCurrentUser() {
        String deviceID = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        if (deviceID == null || deviceID.isEmpty()) {
            Toast.makeText(this, "Unable to get device ID", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseWorker.getUserByDeviceID(deviceID)
                .addOnSuccessListener(user -> {
                    if (user == null) {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update welcome text with user name if available
                    if (homeTitleTextView != null) {
                        String baseText = "Welcome";
                        String userName = user.getName();
                        if (userName != null && !userName.trim().isEmpty()) {
                            homeTitleTextView.setText(baseText + ",\n" + userName.trim());
                        } else {
                            homeTitleTextView.setText(baseText);
                        }
                    }

                    loadSignedUpEvents(user);
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading user data", e);
                    Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSignedUpEvents(RegisteredUser user) {
        if (user.getSigned_up_events() == null || user.getSigned_up_events().isEmpty()) {
            pastEventList.clear();
            pastEventAdapter.notifyDataSetChanged();
            return;
        }

        List<String> eventIDs = new ArrayList<>(user.getSigned_up_events());
        pastEventList.clear();

        final int[] loadedCount = {0};
        for (String eventID : eventIDs) {
            databaseWorker.getEventByID(eventID)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Event event = DatabaseWorker.convertDocumentToEvent(documentSnapshot);
                            if (event != null) {
                                pastEventList.add(event);
                            }
                        }
                        loadedCount[0]++;
                        if (loadedCount[0] == eventIDs.size()) {

                            recyclerViewPastEvents.setVisibility(View.VISIBLE);
                            layoutEmptyState.setVisibility(View.GONE);
                            pastEventAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error loading event: " + eventID, e);
                        loadedCount[0]++;
                        if (loadedCount[0] == eventIDs.size()) {
                            pastEventAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
}