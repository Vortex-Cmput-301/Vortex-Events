package com.example.vortex_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class OrganizerViewParticipant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_view_participant);

        Spinner spinner = findViewById(R.id.participant_filter_dropdown);

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


//        Suggested fix by android studio
        AtomicReference<ArrayList<String>> acceptedList = new AtomicReference<>(new ArrayList<>());
        AtomicReference<ArrayList<String>> waitlistList = new AtomicReference<>(new ArrayList<>());
        AtomicReference<ArrayList<String>> deletedList = new AtomicReference<>(new ArrayList<>());

        DatabaseWorker dbwork = new DatabaseWorker();
        dbwork.getEventByID("6dehsaW").addOnSuccessListener(documentSnapshot -> { // Note: singular
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        Log.d("OrganizerViewParticipant", "Selected item: " + selectedItem);
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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}