package com.example.vortex_events;// In ProfileActivity.java

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Make sure this is imported

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile extends AppCompatActivity{

    private List<Event> pastEventList;
    private MaterialButton buttonDeleteProfile;
    private DatabaseWorker databaseWorker;
    private Users currentUser;
    private DialogHelper dialogHelper; // Added DialogHelper


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

        // Initialize delete profile button - SINGLE initialization (removed duplicate)
        buttonDeleteProfile = findViewById(R.id.button_log_out);
        databaseWorker = new DatabaseWorker();
        currentUser = getCurrentUser(); // Use helper method to get current user
        dialogHelper = new DialogHelper(this); // Initialize DialogHelper
        buttonDeleteProfile.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
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

    /**
     * Get current user from device ID
     * This creates a RegisteredUser instance using the device ID
     */
    private Users getCurrentUser() {
        try {
            // Get device ID from Android system
            @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Create a RegisteredUser with the device ID
            // TODO: Replace with actual user data retrieval
            return new RegisteredUser(deviceID, "unknown", "unknown@example.com", "User");
        } catch (Exception e) {
            Toast.makeText(this, "Error getting user data", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    //Todo this will also get the events from the DATABASE, however right now it is only a TEST
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
    //TODO will switch to events details
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

    /**
     * Show delete confirmation dialog with input validation
     * Simplified version using DialogHelper
     */
    private void showDeleteConfirmationDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        dialogHelper.showDeleteConfirmationDialog(currentUser, this::onDeleteSuccess);
    }

    /**
     * Handle successful delete operation
     */
    private void onDeleteSuccess() {
        // Show 5-second countdown (reduced from 10 seconds for better UX)
        dialogHelper.showExitCountdown(5, this::exitApplication);
    }

    /**
     * Exit the application
     */
    private void exitApplication() {
        finishAffinity(); // Close all activities and exit app
    }

}
