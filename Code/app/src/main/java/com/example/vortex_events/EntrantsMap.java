package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class EntrantsMap extends AppCompatActivity {
    DatabaseWorker dbWorker;
    AtomicReference<ArrayList<String>> acceptedList = new AtomicReference<>(new ArrayList<>());
    AtomicReference<ArrayList<String>> waitlistList = new AtomicReference<>(new ArrayList<>());
    AtomicReference<ArrayList<String>> deletedList = new AtomicReference<>(new ArrayList<>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrants_map);

        Intent returnedID = getIntent();
        String eventID = returnedID.getStringExtra("EventID").toString();

        dbWorker = new DatabaseWorker();

        dbWorker.getEventByID(eventID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("OrganizerViewParticipant", "Event 'accepted' field: " +
                        documentSnapshot.get("accepted"));
                acceptedList.set((ArrayList<String>) documentSnapshot.get("accepted"));
                waitlistList.set((ArrayList<String>) documentSnapshot.get("waitlist"));
                deletedList.set((ArrayList<String>) documentSnapshot.get("declined"));
            } else {
                Log.e("OrganizerViewParticipant", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("OrganizerViewParticipant", "Error getting document", e);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}