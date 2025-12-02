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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Displays and filters participants: accept / waiting list / cancelled
 * Depends on: DatabaseWorker.getParticipants(eventId, ValueEventListener)
 */
public class OrganizerViewParticipant extends AppCompatActivity {

    // UI Elements
    Button notifyGroup;
    Button btnDecline;
    Spinner spinner;
    String eventID;
    private static final String TAG = "OrganizerViewParticipant";


    UserAdapter listAdapter;
    ArrayList<RegisteredUser> acceptedList = new ArrayList<>();
    ArrayList<RegisteredUser> waitlistList = new ArrayList<>();
    ArrayList<RegisteredUser> declinedList = new ArrayList<>();
    ArrayList<RegisteredUser> wonLotteryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view_participant);

        spinner = findViewById(R.id.participant_filter_dropdown);
        btnDecline = findViewById(R.id.btn_decline_selected);
        ListView participantList = findViewById(R.id.participantList);
        notifyGroup = findViewById(R.id.notify_group_btn);
        ImageView downloadButton = findViewById(R.id.imageView);

        Intent returnedID = getIntent();
        eventID = returnedID.getStringExtra("EventID");
        Log.d(TAG, "onCreate: EventID: " + eventID);

        // Initialize Adapter
        listAdapter = new UserAdapter(this, new ArrayList<>());
        participantList.setAdapter(listAdapter);

        // Setup Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.item_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Load Data
        setupParticipantLists(spinner, eventID);

        // LONG CLICK LISTENER
        participantList.setOnItemLongClickListener((parent, view, position, id) -> {
            String currentTab = spinner.getSelectedItem().toString();
            // Only allow selection in Waitlist or Won Lottery
            if (currentTab.equals("Waitlist") || currentTab.equals("WonLottery") || currentTab.equals("Won Lottery")) {
                RegisteredUser user = listAdapter.getItem(position);
                if (user != null) handleSelection(user.getDeviceID());
                return true;
            }
            return false;
        });

        // To toggle selection
        participantList.setOnItemClickListener((parent, view, position, id) -> {
            RegisteredUser user = listAdapter.getItem(position);
            if (user != null && listAdapter.isSelectionMode()) {
                handleSelection(user.getDeviceID());
            }
        });

        // NOTIFY BUTTON
        notifyGroup.setOnClickListener(view -> {
            String mode = spinner.getSelectedItem().toString();
            Intent intent = new Intent(OrganizerViewParticipant.this, OrganizerNotificationsDashboard.class);
            intent.putExtra("notifyMode", mode);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        // DECLINE BUTTON LISTENER
        btnDecline.setOnClickListener(v -> {
            ArrayList<String> selectedIDs = listAdapter.getSelectedIds();
            String currentTab = spinner.getSelectedItem().toString();
            if (!selectedIDs.isEmpty()) {
                moveUsersToDeclined(selectedIDs, currentTab);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        downloadButton.setOnClickListener(v -> {
            CsvExporter.exportAcceptedEntrants(OrganizerViewParticipant.this, eventID);
        });
    }


    private void setupParticipantLists(Spinner spinner, String eventId) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        DatabaseWorker dbwork = new DatabaseWorker(fs);

        dbwork.getEventByID(eventId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> acceptedIds = (ArrayList<String>) documentSnapshot.get("accepted");
                ArrayList<String> waitlistIds = (ArrayList<String>) documentSnapshot.get("waitlist");
                ArrayList<String> declinedIds = (ArrayList<String>) documentSnapshot.get("declined");
                ArrayList<String> wonLotteryIds = (ArrayList<String>) documentSnapshot.get("wonLottery");

                int totalUsers = (acceptedIds != null ? acceptedIds.size() : 0) +
                        (waitlistIds != null ? waitlistIds.size() : 0) +
                        (declinedIds != null ? declinedIds.size() : 0) +
                        (wonLotteryIds != null ? wonLotteryIds.size() : 0);

                if (totalUsers == 0) {
                    setupSpinnerListener(spinner);
                    return;
                }



                AtomicInteger usersLoaded = new AtomicInteger(0);
                Runnable checkDone = () -> {
                    if (usersLoaded.incrementAndGet() == totalUsers) {
                        setupSpinnerListener(spinner);
                    }
                };

                if (acceptedIds != null) {
                    for (String userId : acceptedIds) {
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) acceptedList.add(user);
                            checkDone.run();
                        });
                    }
                }
                if (waitlistIds != null) {
                    for (String userId : waitlistIds) {
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) waitlistList.add(user);
                            checkDone.run();
                        });
                    }
                }
                if (declinedIds != null) {
                    for (String userId : declinedIds) {
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) declinedList.add(user);
                            checkDone.run();
                        });
                    }
                }
                if (wonLotteryIds != null) {
                    for (String userId : wonLotteryIds) {
                        dbwork.getUserByDeviceID(userId).addOnSuccessListener(user -> {
                            if (user != null) wonLotteryList.add(user);
                            checkDone.run();
                        });
                    }
                }
            }
        });
    }


    private void setupSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                listAdapter.clear();
                listAdapter.clearSelection(); // Clear selections when switching tabs
                btnDecline.setVisibility(View.GONE);

                if (selectedItem.equals("Accepted")) {
                    listAdapter.addAll(acceptedList);
                } else if (selectedItem.equals("Waitlist")) {
                    listAdapter.addAll(waitlistList);
                } else if (selectedItem.equals("Declined")) {
                    listAdapter.addAll(declinedList);
                } else if (selectedItem.equals("WonLottery") || selectedItem.equals("Won Lottery")) {
                    listAdapter.addAll(wonLotteryList);
                }
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial Load
        listAdapter.clear();
        listAdapter.addAll(acceptedList);
        listAdapter.notifyDataSetChanged();
    }

    // Handles List of users, Correct DB naming, Batch update
    private void moveUsersToDeclined(ArrayList<String> userIDs, String sourceTab) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        com.google.firebase.firestore.DocumentReference eventRef = db.collection("Events").document(eventID);

        String fieldToRemoveFrom;
        ArrayList<RegisteredUser> sourceList;

        if (sourceTab.equals("Waitlist")) {
            fieldToRemoveFrom = "waitlist";
            sourceList = waitlistList;
        } else if (sourceTab.equals("WonLottery") || sourceTab.equals("Won Lottery")) {
            fieldToRemoveFrom = "wonLottery";
            sourceList = wonLotteryList;
        } else {
            return;
        }


        batch.update(eventRef, fieldToRemoveFrom, FieldValue.arrayRemove(userIDs.toArray()));
        batch.update(eventRef, "declined", FieldValue.arrayUnion(userIDs.toArray()));


        ArrayList<RegisteredUser> finalSourceList = sourceList;
        batch.commit().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Users Moved to Declined", Toast.LENGTH_SHORT).show();


            ArrayList<RegisteredUser> usersToMove = new ArrayList<>();
            for (RegisteredUser u : finalSourceList) {
                if (userIDs.contains(u.getDeviceID())) usersToMove.add(u);
            }
            finalSourceList.removeAll(usersToMove);
            declinedList.addAll(usersToMove);

            // Refresh List
            listAdapter.clear();
            listAdapter.addAll(finalSourceList);
            listAdapter.clearSelection();
            btnDecline.setVisibility(View.GONE);

        }).addOnFailureListener(e -> Log.e(TAG, "Move failed", e));
    }

    private void handleSelection(String deviceID) {
        listAdapter.toggleSelection(deviceID);
        if (listAdapter.isSelectionMode()) {
            btnDecline.setVisibility(View.VISIBLE);
            btnDecline.setText("Decline (" + listAdapter.getSelectedIds().size() + ")");
        } else {
            btnDecline.setVisibility(View.GONE);
        }
    }
}
