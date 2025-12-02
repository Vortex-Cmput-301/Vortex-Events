package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Displays and filters participants: accept / waiting list / cancelled
 * Depends on: DatabaseWorker.getParticipants(eventId, ValueEventListener)
 */
public class OrganizerViewParticipant extends AppCompatActivity {
    Button notifyGroup;
    String eventID;
    private static final String TAG = "OrganizerViewParticipant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view_participant);
        Spinner spinner = findViewById(R.id.participant_filter_dropdown);

        Intent returnedID = getIntent();
        eventID = returnedID.getStringExtra("EventID");
        Log.d(TAG, "onCreate: EventID: " + eventID);

        notifyGroup = findViewById(R.id.notify_group_btn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.item_dropdown,
                android.R.layout.simple_spinner_item
        );

        ListView participantList = findViewById(R.id.participantList);
        UserAdapter listAdapter = new UserAdapter(this, new ArrayList<>());
        participantList.setAdapter(listAdapter);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        setupParticipantLists(spinner, listAdapter, eventID);

        notifyGroup.setOnClickListener(view -> {
            String mode = spinner.getSelectedItem().toString();
            Intent intent = new Intent(OrganizerViewParticipant.this, OrganizerNotificationsDashboard.class);
            intent.putExtra("notifyMode", mode);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView downloadButton = findViewById(R.id.imageView);
        setupParticipantLists(spinner, listAdapter, eventID);
        downloadButton.setOnClickListener(v -> {
            CsvExporter.exportAcceptedEntrants(
                    OrganizerViewParticipant.this,
                    eventID
            );
        });
    }

    private void setupParticipantLists(Spinner spinner, UserAdapter listAdapter, String eventId) {
        ArrayList<RegisteredUser> acceptedList = new ArrayList<>();
        ArrayList<RegisteredUser> waitlistList = new ArrayList<>();
        ArrayList<RegisteredUser> deletedList = new ArrayList<>();
        ArrayList<RegisteredUser> wonLotteryList = new ArrayList<>();

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        DatabaseWorker dbwork = new DatabaseWorker(fs);

        dbwork.getEventByID(eventId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "Event document found. Data: " + documentSnapshot.getData());
                ArrayList<String> acceptedIds = (ArrayList<String>) documentSnapshot.get("accepted");
                ArrayList<String> waitlistIds = (ArrayList<String>) documentSnapshot.get("waitlist");
                ArrayList<String> declinedIds = (ArrayList<String>) documentSnapshot.get("declined");

                ArrayList<String> wonLotteryIds = (ArrayList<String>) documentSnapshot.get("wonLottery");

                Log.d(TAG, "Accepted IDs: " + acceptedIds);
                Log.d(TAG, "Waitlist IDs: " + waitlistIds);
                Log.d(TAG, "Declined IDs: " + declinedIds);

                Log.d(TAG, "WonLottery IDs: " + wonLotteryIds);

                int totalUsers = (acceptedIds != null ? acceptedIds.size() : 0) +
                                 (waitlistIds != null ? waitlistIds.size() : 0) +
                                 (declinedIds != null ? declinedIds.size() : 0) + (wonLotteryIds != null ? wonLotteryIds.size() : 0);

                Log.d(TAG, "Total users to load: " + totalUsers);

                if (totalUsers == 0) {
                    Log.d(TAG, "No users to load, setting up spinner immediately.");
                    setupSpinnerListener(spinner, listAdapter, acceptedList, waitlistList, deletedList,wonLotteryList);
                    return;
                }

                AtomicInteger usersLoaded = new AtomicInteger(0);

                if (acceptedIds != null) {
                    for (String userId : acceptedIds) {
                        Log.d(TAG, "Fetching user: " + userId);
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) {
                                Log.d(TAG, "User found: " + user.getName());
                                acceptedList.add(user);
                            } else {
                                Log.d(TAG, "User not found for ID: " + userId);
                            }
                            if (usersLoaded.incrementAndGet() == totalUsers) {
                                Log.d(TAG, "All users loaded. Setting up spinner.");
                                setupSpinnerListener(spinner, listAdapter, acceptedList, waitlistList, deletedList, wonLotteryList);
                            }
                        });
                    }
                }
                if (waitlistIds != null) {
                    for (String userId : waitlistIds) {
                        Log.d(TAG, "Fetching user: " + userId);
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) {
                                Log.d(TAG, "User found: " + user.getName());
                                waitlistList.add(user);
                            } else {
                                Log.d(TAG, "User not found for ID: " + userId);
                            }
                            if (usersLoaded.incrementAndGet() == totalUsers) {
                                Log.d(TAG, "All users loaded. Setting up spinner.");
                                setupSpinnerListener(spinner, listAdapter, acceptedList, waitlistList, deletedList, wonLotteryList);
                            }
                        });
                    }
                }
                if (declinedIds != null) {
                    for (String userId : declinedIds) {
                        Log.d(TAG, "Fetching user: " + userId);
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) {
                                Log.d(TAG, "User found: " + user.getName());
                                deletedList.add(user);
                            } else {
                                Log.d(TAG, "User not found for ID: " + userId);
                            }
                            if (usersLoaded.incrementAndGet() == totalUsers) {
                                Log.d(TAG, "All users loaded. Setting up spinner.");
                                setupSpinnerListener(spinner, listAdapter, acceptedList, waitlistList, deletedList, wonLotteryList);
                            }
                        });
                    }
                }
                if (wonLotteryIds != null) {
                    for (String userId : wonLotteryIds) {
                        Log.d(TAG, "Fetching user: " + userId);
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) {
                                Log.d(TAG, "User found: " + user.getName());
                                wonLotteryList.add(user);
                            } else {
                                Log.d(TAG, "User not found for ID: " + userId);
                            }
                            if (usersLoaded.incrementAndGet() == totalUsers) {
                                Log.d(TAG, "All users loaded. Setting up spinner.");
                                setupSpinnerListener(spinner, listAdapter, acceptedList, waitlistList, deletedList, wonLotteryList);
                            }
                        });
                    }
                }

            } else {
                Log.e(TAG, "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting document", e);
        });
    }

    private void setupSpinnerListener(Spinner spinner, UserAdapter listAdapter,
                                      ArrayList<RegisteredUser> acceptedList,
                                      ArrayList<RegisteredUser> waitlistList,
                                      ArrayList<RegisteredUser> deletedList,
                                      ArrayList<RegisteredUser> wonLotteryList) {
        Log.d(TAG, "setupSpinnerListener called.");
        Log.d(TAG, "Final Accepted list size: " + acceptedList.size());
        Log.d(TAG, "Final Waitlist list size: " + waitlistList.size());
        Log.d(TAG, "Final Declined list size: " + deletedList.size());
        Log.d(TAG, "Final WonLottery list size: " + wonLotteryList.size());

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        Log.d(TAG, "Spinner item selected: " + selectedItem);
                        listAdapter.clear();
                        if (selectedItem.equals("Accepted")) {
                            Log.d(TAG, "Adding acceptedList to adapter. Size: " + acceptedList.size());
                            listAdapter.addAll(acceptedList);
                        } else if (selectedItem.equals("Waitlist")) {
                            Log.d(TAG, "Adding waitlistList to adapter. Size: " + waitlistList.size());
                            listAdapter.addAll(waitlistList);
                        } else if (selectedItem.equals("Declined")) {
                            Log.d(TAG, "Adding deletedList to adapter. Size: " + deletedList.size());
                            listAdapter.addAll(deletedList);
                        } else if (selectedItem.equals("WonLottery")) {
                            Log.d(TAG, "Adding wonLotteryList to adapter. Size: " + wonLotteryList.size());
                            listAdapter.addAll(wonLotteryList);
                        }
                        Log.d(TAG, "Adapter count after addAll: " + listAdapter.getCount());
                        listAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // required but not used
                    }
                });

        // Initially load accepted list
        Log.d(TAG, "Initially loading accepted list.");
        listAdapter.clear();
        listAdapter.addAll(acceptedList);
        Log.d(TAG, "Adapter count after initial load: " + listAdapter.getCount());
        listAdapter.notifyDataSetChanged();
    }
}
