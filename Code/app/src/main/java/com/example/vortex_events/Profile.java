package com.example.vortex_events;// In ProfileActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Make sure this is imported

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile extends AppCompatActivity{

    private List<Event> pastEventList;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_past_events);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupPastEventListData();

        PastEventAdapter pastEventAdapter = new PastEventAdapter(pastEventList,this);

        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        recyclerView.setAdapter(pastEventAdapter);

        View back_button = findViewById(R.id.button_back);

        back_button.setOnClickListener(v -> {
            finish();
        });

        View account_settings = findViewById((R.id.button_account_settings));

        account_settings.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), Account_settings.class);
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
                } else if (itemId == R.id.button_back) {
                    Intent intent = new Intent(getApplicationContext(), ExplorePage.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void setupPastEventListData() {
        pastEventList = new ArrayList<>();
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


        Event first_event = new Event("Scream", "UofA", "Bonnie", "123456", enrollmentStart, enrollmentEnd, eventDate, eventDate, arraylist, "description", 20);
        pastEventList.add(first_event);
    }


    /**
     * This method is called by the PastEventAdapter when a details button is clicked.
     * @param position The position of the item that was clicked.
     */
    public void onPastEventDetailsClick(int position) {
        // Safety check
        if (position < 0 || position >= pastEventList.size()) {
            return;
        }
        Event clickedEvent = pastEventList.get(position);

        // Intent intent = new Intent(this, EventDetailsActivity.class);
        // intent.putExtra("EVENT_ID", clickedEvent.getEventID());
        // startActivity(intent);
    }

}

