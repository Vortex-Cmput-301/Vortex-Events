package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Make sure this is imported

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile extends AppCompatActivity {

    public static final String EXTRA_DEVICE_ID = "extra_device_id";

    private List<Event> pastEventList;
    private String targetDeviceID;   // the user whose profile will be displayed FOR ADMIN USE ONLY

    private MaterialButton buttonDeleteProfile;
    private DatabaseWorker databaseWorker;
    private RegisteredUser currentUser;
    private DialogHelper dialogHelper;
    private PastEventAdapter pastEventAdapter;
    private RecyclerView recyclerView;

    // Control flag for switching between event display modes
    private final boolean SHOW_SIGNED_UP_EVENTS = true;

    // whether the current device user is admin
    private boolean isCurrentUserAdmin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView_past_events);

        // Initialize empty list and adapter
        pastEventList = new ArrayList<>();
        pastEventAdapter = new PastEventAdapter(pastEventList, this);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pastEventAdapter);

        // Initialize DatabaseWorker
        databaseWorker = new DatabaseWorker();

        // Read optional deviceID passed from AdminActivity
        targetDeviceID = getIntent().getStringExtra(EXTRA_DEVICE_ID);

        View back_button = findViewById(R.id.button_back);
        View userInitialView = findViewById(R.id.textView_user_initial);
        View account_settings = findViewById((R.id.button_account_settings));


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        account_settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountSettings.class);
            startActivity(intent);
        });

        // Initialize delete profile button
        buttonDeleteProfile = findViewById(R.id.button_log_out);
        dialogHelper = new DialogHelper(this);

        // decide which user to load based on targetDeviceID
        if (targetDeviceID == null || targetDeviceID.isEmpty()) {
            // Normal user: view own profile
            // get current device ID
            @SuppressLint("HardwareIds")
            String currentDeviceID = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );

            // load full RegisteredUser from database to know type/name/etc
            loadUserByDeviceID(currentDeviceID, new UserLoadCallback() {
                @Override
                public void onUserLoaded(RegisteredUser user) {
                    if (user == null) {
                        Toast.makeText(Profile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentUser = user;
                    // Determine if this user is admin based on type field
                    String type = currentUser.getType();
                    isCurrentUserAdmin = (type != null && type.equals("Admin"));
                    Log.d("Profile", "Loaded user type: " + currentUser.getType());
                    // Delete button is available for current user
                    buttonDeleteProfile.setVisibility(View.VISIBLE);
                    buttonDeleteProfile.setOnClickListener(v -> showDeleteConfirmationDialog());

                    // Bind header with user info
                    bindUserHeader(currentUser);

                    // Only allow long-press to open Admin when:
                    // - this is the current device user's own profile (targetDeviceID == null)
                    // - the current user is admin
                    if (isCurrentUserAdmin && userInitialView != null) {
                        userInitialView.setOnLongClickListener(v -> {
                            Intent intent = new Intent(Profile.this, AdminActivity.class);
                            startActivity(intent);
                            return true;
                        });
                    }

                    // Load data for this user
                    setupPastEventListData();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(Profile.this, "Error loading current user profile.", Toast.LENGTH_SHORT).show();
                    Log.e("Profile", "Error loading current user by deviceID", e);
                }
            });

        } else {
            // Admin viewing another user's profile
            buttonDeleteProfile.setVisibility(View.VISIBLE);
            buttonDeleteProfile.setOnClickListener(v -> {
                // Admin should be able to delete any user.
                if (currentUser != null) {
                    // Normal path: we have a full RegisteredUser object
                    dialogHelper.showDeleteConfirmationDialog(currentUser, () -> {
                        Toast.makeText(Profile.this, "User profile deleted.", Toast.LENGTH_SHORT).show();
                        finish(); // return to AdminActivity
                    });
                } else if (targetDeviceID != null && !targetDeviceID.isEmpty()) {
                    // Fallback: cannot convert document to RegisteredUser,
                    // but we still know the deviceID. Create a minimal user object
                    // that only carries deviceID so deleteUser() can work.
                    RegisteredUser tempUser = new RegisteredUser(
                            targetDeviceID,
                            "unknown",            // phone_number placeholder
                            "unknown@example.com",// email placeholder
                            "Unknown User",       // name placeholder
                            "randomw tokenb",
                            0.0,                  // latitude placeholder
                            0.0,                  // longitude placeholder
                            "Guest" ,
                            false// type placeholder
                    );

//                    GuestUser tempUser = new GuestUser(this);

                    dialogHelper.showDeleteConfirmationDialog(tempUser, () -> {
                        Toast.makeText(Profile.this, "User profile deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    Toast.makeText(Profile.this, "User data not available", Toast.LENGTH_SHORT).show();
                }
            });

            loadUserByDeviceID(targetDeviceID, new UserLoadCallback() {
                @Override
                public void onUserLoaded(RegisteredUser user) {
                    currentUser = user;
                    if (currentUser != null) {
                        bindUserHeader(currentUser);
                        setupPastEventListData();
                    } else {
                        // Document exists but cannot be fully converted.
                        // We keep header generic; delete will use fallback tempUser.
                        Toast.makeText(Profile.this, "User data cannot be fully loaded.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(Profile.this, "Error loading user profile.", Toast.LENGTH_SHORT).show();
                    Log.e("Profile", "Error loading user by deviceID", e);
                }
            });
        }

        // Find the Past Events button
        MaterialButton pastEventsButton = findViewById(R.id.button_my_events);
        pastEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HistoryEvents.class);
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

    }

    private interface UserLoadCallback {
        void onUserLoaded(RegisteredUser user);
        void onError(Exception e);
    }

    private void loadUserByDeviceID(String deviceID, UserLoadCallback callback) {
        databaseWorker.getUserByDeviceID(deviceID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                RegisteredUser user = task.getResult();
                if (callback != null) {
                    callback.onUserLoaded(user);
                }
            } else {
                if (callback != null) {
                    callback.onError(task.getException());
                }
            }
        });
    }

    /**
     * Setup past event list data - now shows signed up events by default
     */
    private void setupPastEventListData() {
        // clear the list
        pastEventList.clear();

        if (SHOW_SIGNED_UP_EVENTS) {
            // show signed up events
            loadSignedUpEvents();
        } else {
            // show historical events
            loadHistoricalEvents();
        }
    }

    /**
     * Load signed up events from database
     */
    private void loadSignedUpEvents() {
        if (currentUser == null) {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add loading message
        Toast.makeText(this, "Loading events...", Toast.LENGTH_SHORT).show();

        // find User data from database to get signed_up_events
        databaseWorker.getUserByDeviceID(currentUser.getDeviceID()).addOnSuccessListener(user -> {
            if (user != null && user.getSigned_up_events() != null) {
                List<String> signedUpEventIDs = user.getSigned_up_events();

                if (signedUpEventIDs.isEmpty()) {
                    // No signed up events found
                    pastEventList.clear();
                    Toast.makeText(this, "No signed up events found", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                    return;
                }

                // load all events find
                loadEventsFromIDs(signedUpEventIDs);
            } else {
                pastEventList.clear();
                Toast.makeText(this, "No signed up events available", Toast.LENGTH_SHORT).show();
                updateRecyclerView();
            }
        }).addOnFailureListener(e -> {
            Log.e("Profile", "Error loading user data: " + e.getMessage());
            Toast.makeText(this, "Error loading events, showing historical events", Toast.LENGTH_SHORT).show();
            // fallback to historical(sample) events
            loadHistoricalEvents();
        });
    }

    /**
     * Load events from a list of event IDs
     */
    private void loadEventsFromIDs(List<String> eventIDs) {
        pastEventList.clear();

        if (eventIDs.isEmpty()) {
            updateRecyclerView();
            return;
        }

        final int[] loadedCount = {0};

        for (String eventID : eventIDs) {
            databaseWorker.getEventByID(eventID).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Event event = DatabaseWorker.convertDocumentToEvent(documentSnapshot);
                    if (event != null) {
                        pastEventList.add(event);
                    }
                }
                loadedCount[0]++;

                // check if all events have been loaded
                if (loadedCount[0] == eventIDs.size()) {
                    // All events loaded, update RecyclerView
                    updateRecyclerView();

                    if (pastEventList.isEmpty()) {
                        Toast.makeText(this, "No valid events found", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Loaded " + pastEventList.size() + " events", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("Profile", "Error loading event: " + eventID, e);
                loadedCount[0]++;
                if (loadedCount[0] == eventIDs.size()) {
                    updateRecyclerView();
                }
            });
        }
    }

    /**
     * Update the RecyclerView with the loaded events
     */
    private void updateRecyclerView() {
        runOnUiThread(() -> {
            if (pastEventAdapter != null) {
                pastEventAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Load historical events (original hardcoded implementation)
     */
    private void loadHistoricalEvents() {
        pastEventList = new ArrayList<>();
        ArrayList<String> arraylist = new ArrayList<>();
        arraylist.add("trending");
        arraylist.add("local");

        Calendar calendar = Calendar.getInstance();

        // Create the enrollment start date: October 1, 2025
        calendar.set(2025, Calendar.OCTOBER, 1);
        Date enrollmentStart = calendar.getTime();

        // Create the enrollment end date: November 15, 2025
        calendar.set(2025, Calendar.NOVEMBER, 15);
        Date enrollmentEnd = calendar.getTime();

        // Create the event start/end date: November 20, 2025
        calendar.set(2025, Calendar.NOVEMBER, 20);
        Date eventDate = calendar.getTime();

        Event first_event = new Event(
                "Scream",
                "UofA",
                "Bonnie",
                "123456",
                enrollmentStart,
                enrollmentEnd,
                eventDate,
                eventDate,
                arraylist,
                "description",
                20
        );
        pastEventList.add(first_event);
    }

    /**
     * This method is called by the PastEventAdapter when a details button is clicked.
     * @param position The position of the item that was clicked.
     */
    public void onPastEventDetailsClick(int position) {
        if (position < 0 || position >= pastEventList.size()) {
            return;
        }
        Event clickedEvent = pastEventList.get(position);
        // TODO: open event details if needed
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
        dialogHelper.showExitCountdown(5, this::exitApplication);
    }

    private void exitApplication() {
        finishAffinity();
    }

    /**
     * Bind currentUser to profile header views.
     * This sets the name text and initial letter.
     */
    private void bindUserHeader(RegisteredUser user) {
        if (user == null) {
            return;
        }

        View initialView = findViewById(R.id.textView_user_initial);
        View nameView = findViewById(R.id.textView_user_name);

        if (initialView instanceof android.widget.TextView) {
            android.widget.TextView tvInitial = (android.widget.TextView) initialView;
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                char firstChar = Character.toUpperCase(name.charAt(0));
                tvInitial.setText(String.valueOf(firstChar));
            } else {
                tvInitial.setText("U");
            }
        }

        if (nameView instanceof android.widget.TextView) {
            android.widget.TextView tvName = (android.widget.TextView) nameView;
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                tvName.setText(name);
            } else {
                tvName.setText("Unknown User");
            }
        }
    }
}
