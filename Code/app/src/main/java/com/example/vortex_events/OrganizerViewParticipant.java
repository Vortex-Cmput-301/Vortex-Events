package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Displays and filters participants: accept / waiting list / cancelled
 * Depends on: DatabaseWorker.getParticipants(eventId, ValueEventListener)
 */
public class OrganizerViewParticipant extends AppCompatActivity {
    Button notifyGroup;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view_participant);
        Spinner spinner = findViewById(R.id.participant_filter_dropdown);

        Intent returnedID = getIntent();
        eventID = returnedID.getStringExtra("EventID");

        notifyGroup = findViewById(R.id.notify_group_btn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.item_dropdown,
                android.R.layout.simple_spinner_item
        );

        ListView participantList = findViewById(R.id.participantList);
        ArrayList<String> loading = new ArrayList<>();
        loading.add("Loading...");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                loading);
        participantList.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        listAdapter.notifyDataSetChanged();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



//        TODO: get event id from intent || DONE
        setupParticipantLists(spinner, listAdapter, eventID);

        notifyGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mode = spinner.getSelectedItem().toString();
                Intent intent = new Intent(OrganizerViewParticipant.this, OrganizerNotificationsDashboard.class);
                intent.putExtra("notifyMode", mode);
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }

    /**
    * This function setups a spinner with data from an event on the firebase
    * @param spinner: the spinner to setup
    * @param listAdapter: the adapter to use for the list view
    * @param eventId: the id of the event to get the data from
    * */
    private void setupParticipantLists(Spinner spinner, ArrayAdapter<String> listAdapter, String eventId) {
        //        Suggested fix by android studio
        AtomicReference<ArrayList<String>> acceptedList = new AtomicReference<>(new ArrayList<>());
        AtomicReference<ArrayList<String>> waitlistList = new AtomicReference<>(new ArrayList<>());
        AtomicReference<ArrayList<String>> deletedList = new AtomicReference<>(new ArrayList<>());

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        DatabaseWorker dbwork = new DatabaseWorker(fs);

//        get event by id
        dbwork.getEventByID(eventId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("OrganizerViewParticipant", "Event 'accepted' field: " +
                        documentSnapshot.get("accepted"));
                acceptedList.set((ArrayList<String>) documentSnapshot.get("accepted"));
                waitlistList.set((ArrayList<String>) documentSnapshot.get("waitlist"));
                deletedList.set((ArrayList<String>) documentSnapshot.get("declined"));
                listAdapter.clear();
                listAdapter.addAll(acceptedList.get());
                listAdapter.notifyDataSetChanged();
            } else {
                Log.e("OrganizerViewParticipant", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("OrganizerViewParticipant", "Error getting document", e);
        });

        // setup spinner
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        Log.d("OrganizerViewParticipant", "Selected item: " + selectedItem);
//                        check which item was selected
                        if (selectedItem.equals("Accepted")) {
                            Log.d("OrganizerViewParticipant", "Accepted");
                            listAdapter.clear();
                            listAdapter.addAll(acceptedList.get());
                            listAdapter.notifyDataSetChanged();
                        } else if (selectedItem.equals("Waitlist")) {
                            Log.d("OrganizerViewParticipant", "Waitlist");
                            listAdapter.clear();
                            listAdapter.addAll(waitlistList.get());
                            listAdapter.notifyDataSetChanged();
                        } else if (selectedItem.equals("Declined")) {
                            Log.d("OrganizerViewParticipant", "Declined");
                            listAdapter.clear();
                            listAdapter.addAll(deletedList.get());
                            listAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // required but not used
                    }
                });
    }
}