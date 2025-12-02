package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity showing a user's historical events (past events) with a list view.
 */
public class HistoryEvents extends AppCompatActivity {

    private ListView eventListView;
    private ImageView backButton;
    private TextView titleTextView;

    private RegisteredUser currentUser;
    private DatabaseWorker databaseWorker;
    private List<HistoryEventItem> historyEvents;
    private HistoryEventAdapter adapter;

    /**
     * Initialize the activity, views, and start loading history events.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_history); // Use the provided layout file

        // Initialize views
        eventListView = findViewById(R.id.event_list);
        backButton = findViewById(R.id.button_back);
        titleTextView = findViewById(R.id.textView_profile_title);

        // Initialize database worker
        databaseWorker = new DatabaseWorker(FirebaseFirestore.getInstance());

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load current user and history events
        loadCurrentUserAndHistory();

    }

    /**
     * Loads the current user by device ID and then loads their history.
     */
    private void loadCurrentUserAndHistory() {
        // Get device ID
        String deviceID = getDeviceID();
        if (deviceID == null || deviceID.isEmpty()) {
            Toast.makeText(this, "Unable to get device ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user from database using device ID
        databaseWorker.getUserByDeviceID(deviceID).addOnCompleteListener(new OnCompleteListener<RegisteredUser>() {
            @Override
            public void onComplete(Task<RegisteredUser> task) {
                if (task.isSuccessful()) {
                    currentUser = task.getResult();
                    if (currentUser != null) {
                        // User found, load history events
                        loadHistoryEvents();
                    } else {
                        Toast.makeText(HistoryEvents.this, "User not found in database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoryEvents.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("HardwareIds")
    /**
     * Returns the Android device ID used to look up the registered user.
     * @return device ID string or null if unavailable
     */
    private String getDeviceID() {
        try {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Loads historical events for the current user and displays them.
     */
    private void loadHistoryEvents() {
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get historical event IDs from user
        List<String> historicalEventIDs = currentUser.getHistoricalEventIDs();

        if (historicalEventIDs.isEmpty()) {
            Toast.makeText(this, "No history events found", Toast.LENGTH_SHORT).show();
            return;
        }

        historyEvents = new ArrayList<>();
        adapter = new HistoryEventAdapter(this, historyEvents);
        eventListView.setAdapter(adapter);

        // Load each event's details
        for (String eventID : historicalEventIDs) {
            loadEventDetails(eventID);
        }
    }

    /**
     * Loads one event's details by ID and adds it to the history list.
     * @param eventID id of the event to load
     */
    private void loadEventDetails(String eventID) {
        databaseWorker.getEventByID(eventID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Convert document to Event object
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            // Get event status from user's history
                            String status = currentUser.getEventStatus(eventID);


                            // Format date from start_time and end_time
                            String formattedDate = formatEventDate(event.getStart_time(), event.getEnd_time());

                            // Create history event item
                            HistoryEventItem historyEvent = new HistoryEventItem(
                                    eventID,
                                    event.getName(),
                                    event.getLocation(),
                                    formattedDate,
                                    status,
                                    event.getImage()
                            );

                            // Add to list and update adapter
                            historyEvents.add(historyEvent);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // Event document doesn't exist, create a placeholder
                        HistoryEventItem historyEvent = new HistoryEventItem(
                                eventID,
                                "Event Not Found",
                                "Unknown Location",
                                "Unknown Date",
                                currentUser.getEventStatus(eventID),
                                null
                        );
                        historyEvents.add(historyEvent);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(HistoryEvents.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Format start_time and end_time into a single date string
     * Format: "MMM dd, yyyy hh:mm a - hh:mm a"
     */
    /**
     * Formats start/end times into a readable date string.
     * @param startTime event start time (nullable)
     * @param endTime event end time (nullable)
     * @return formatted date string
     */
    private String formatEventDate(Date startTime, Date endTime) {
        if (startTime == null && endTime == null) {
            return "Date not specified";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            if (startTime != null && endTime != null) {
                // Both dates available - show full range
                String datePart = dateFormat.format(startTime);
                String startTimePart = timeFormat.format(startTime);
                String endTimePart = timeFormat.format(endTime);
                return datePart + " " + startTimePart + " - " + endTimePart;
            } else if (startTime != null) {
                // Only start time available
                return dateFormat.format(startTime) + " " + timeFormat.format(startTime);
            } else {
                // Only end time available
                return dateFormat.format(endTime) + " " + timeFormat.format(endTime);
            }
        } catch (Exception e) {
            return "Invalid date format";
        }
    }

    // Custom adapter for history events
    private class HistoryEventAdapter extends ArrayAdapter<HistoryEventItem> {

        /**
         * Adapter constructor for history event items.
         */
        public HistoryEventAdapter(HistoryEvents context, List<HistoryEventItem> events) {
            super(context, R.layout.item_past_event, events);
        }

    /**
     * Bind a HistoryEventItem to a list item view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_past_event, parent, false);
            }

            HistoryEventItem event = getItem(position);

            if (event == null) {
                return convertView;
            }

            // Get views
            ImageView thumbnail = convertView.findViewById(R.id.past_event_thumbnail);
            TextView title = convertView.findViewById(R.id.past_event_title);
            TextView location = convertView.findViewById(R.id.textView_event_location);
            TextView date = convertView.findViewById(R.id.past_event_date);
            Button stateButton = convertView.findViewById(R.id.past_event_details_button);

            // Set data
            title.setText(event.getEventName());
            location.setText(event.getEventLocation());
            date.setText(event.getEventDate());

            String imageString = event.getImageBase64(); // *** NEW
            if (imageString != null && !imageString.isEmpty()) { // *** NEW
                try {
                    byte[] decodedString = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT);

                    com.bumptech.glide.Glide.with(convertView.getContext())
                            .load(decodedString)
                            .centerCrop()
                            .into(thumbnail);

                } catch (Exception e) {
                    thumbnail.setImageResource(R.drawable.app_icon);
                }
            } else {
                thumbnail.setImageResource(R.drawable.app_icon);
            }

            // Set button text based on status
            String statusText = getStatusText(event.getEventStatus());
            stateButton.setText(statusText);

            // Set button color based on status (optional)
            setButtonColor(stateButton, event.getEventStatus());

            // Set click listener for details button
            stateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show event details or perform action based on status
                    showEventDetails(event);
                }
            });

            return convertView;
        }

    /**
     * Convert an internal status code into a human-readable label.
     */
    private String getStatusText(String status) {
            if (status == null) {
                return "Unknown";
            }

            switch (status) {
                case RegisteredUser.STATUS_ACCEPTED:
                    return "Accepted";
                case RegisteredUser.STATUS_DECLINED:
                    return "Declined";
                case RegisteredUser.STATUS_CANCELLED:
                    return "Cancelled";
                case RegisteredUser.STATUS_NOT_CHOSEN:
                    return "Not Chosen";
                default:
                    return "Unknown";
            }
        }

    /**
     * Set the button color based on the event status.
     */
    private void setButtonColor(Button button, String status) {
            if (status == null) {
                button.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                return;
            }

            // Set different colors based on status
            switch (status) {
                case RegisteredUser.STATUS_ACCEPTED:
                    button.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
                    break;
                case RegisteredUser.STATUS_DECLINED:
                    button.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_red_light));
                    break;
                case RegisteredUser.STATUS_CANCELLED:
                    button.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                    break;
                case RegisteredUser.STATUS_NOT_CHOSEN:
                    button.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_orange_light));
                    break;
                default:
                    button.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }

    /**
     * Show a toast with basic event details (used by the adapter).
     */
    private void showEventDetails(HistoryEventItem event) {
            // Show event details - could be a dialog or new activity
            Toast.makeText(getContext(),
                    "Event: " + event.getEventName() +
                            "\nLocation: " + event.getEventLocation() +
                            "\nDate: " + event.getEventDate() +
                            "\nStatus: " + getStatusText(event.getEventStatus()),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Data model for history event items
    /**
     * Simple data holder for history list items.
     */
    private class HistoryEventItem {
        private String eventID;
        private String eventName;
        private String eventLocation;
        private String eventDate;
        private String eventStatus;
        private String imageBase64;

        public HistoryEventItem(String eventID, String eventName, String eventLocation,
                                String eventDate, String eventStatus, String imageBase64) {
            this.eventID = eventID;
            this.eventName = eventName;
            this.eventLocation = eventLocation;
            this.eventDate = eventDate;
            this.eventStatus = eventStatus;
            this.imageBase64 = imageBase64;
        }

        // Getters
        public String getEventID() { return eventID; }
        public String getEventName() { return eventName; }
        public String getEventLocation() { return eventLocation; }
        public String getEventDate() { return eventDate; }
        public String getEventStatus() { return eventStatus; }
        public String getImageBase64() { return imageBase64; }
    }
}
