package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExplorePage extends AppCompatActivity {

    private List<Event> eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore_page);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_events);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupEventListData();//adds events to list
        EventAdapter eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        View imageView_profile = findViewById(R.id.imageView_profile);

        imageView_profile.setOnClickListener(v -> {
            // Create an Intent to launch ProfileActivity
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //Add the rest of the activities when finished
            //made a boolean function to implement highlighting items. will implement later
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_create) {
                    Intent intent = new Intent(getApplicationContext(), CreateActivityEvents.class);
                    startActivity(intent);
                    return true;
                }
                    return false;
            }
        });
    }

        //TODO THis is just a test to see if events list work, need to get events from DATABASE in order to finish.
        private void setupEventListData() {
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


