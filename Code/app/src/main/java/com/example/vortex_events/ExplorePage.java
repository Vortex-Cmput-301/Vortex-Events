package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExplorePage extends AppCompatActivity {

    /**
     * Activity that displays a scrollable list of events for users to explore.
     */

    private List<Event> eventList;
    private EventAdapter eventAdapter; //set up adapter
    private DatabaseWorker databaseWorker; //set up database worker


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore_page);

        databaseWorker = new DatabaseWorker(); // Initialize the database worker

        RecyclerView recyclerView = findViewById(R.id.recyclerView_events);

        // Initialize eventList only once
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        loadEventsFromDatabase();

        View imageView_profile = findViewById(R.id.imageView_profile);

        imageView_profile.setOnClickListener(v -> {
            // Create an Intent to launch ProfileActivity
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            intent.putExtra("prev_activity", "explore");
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_explore);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //Add the rest of the activities when finished
            //made a boolean function to implement highlighting items. will implement later
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

        //TODO THis is just a test to see if events list work, need to get events from DATABASE in order to finish.

    /*
     * Load events from database
     */
    // Load events from database
    private void loadEventsFromDatabase() {
        databaseWorker.getAllEvents().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eventList.clear();

                    for (DocumentSnapshot document : task.getResult()) {
                        Event event = DatabaseWorker.convertDocumentToEvent(document);
                        if (event != null) {
                            eventList.add(event);
                        }
                    }

                    eventAdapter.notifyDataSetChanged();

                    Log.d("ExplorePage", "Loaded " + eventList.size() + " events from database");

                    // If no events found, use test data
                    if (eventList.isEmpty()) {
                        setupTestEventListData();
                    }

                } else {
                    Log.e("ExplorePage", "Error getting events", task.getException());
                    setupTestEventListData();
                }
            }
        });
    }

    /**
     * Handles event details click from EventAdapter
     * @param event The event to show details for
     */
    public void onEventDetailsClick(Event event) {
        // TODO: Implement event details navigation
        Intent intent = new Intent(this, EventDetails.class);
        intent.putExtra("EventID", event.getEventID());
        intent.putExtra("EventName", event.getName());
        intent.putExtra("EventLocation", event.getLocation());
        intent.putExtra("EventDate", event.getStart_time() != null ?
                event.getStart_time().getTime() : 0);
        intent.putExtra("EventDescription", event.getDescription());
        intent.putExtra("prev_activity", "explore");
        startActivity(intent);
    }

    private void setupTestEventListData() {
        eventList = new ArrayList<>();
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add("trending");
        arraylist.add("local");

        Calendar calendar = Calendar.getInstance();

        // Create the enrollment start date: October 1, 2025
        calendar.set(2025, Calendar.OCTOBER, 1); // Month is 0-indexed, OCTOBER is 9
        Date enrollmentStart = calendar.getTime();

        // Create the enrollment end date: November 15, 2025
        calendar.set(2025, Calendar.NOVEMBER, 15); // NOVEMBER is 10
        Date enrollmentEnd = calendar.getTime();

        // Create the event start/end date: November 20, 2025
        calendar.set(2025, Calendar.NOVEMBER, 20);
        Date eventDate = calendar.getTime();


        Event first_event = new Event("Scream", "UofA", "Bonnie", "6dehsaW", enrollmentStart, enrollmentEnd, eventDate, eventDate, arraylist, "description", 20);
        eventList.add(first_event);
    }
}


