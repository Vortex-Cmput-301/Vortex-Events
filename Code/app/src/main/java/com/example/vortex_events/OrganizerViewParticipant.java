package com.example.vortex_events;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays and filters participants: accept / waiting list / cancelled
 * Depends on: DatabaseWorker.getParticipants(eventId, ValueEventListener)
 */
public class OrganizerViewParticipant extends AppCompatActivity {

    // Spinner for filter options and ListView for displaying participants
    private Spinner spinnerFilter;
    private ListView participantListView;

    // Adapter and data lists
    private ParticipantAdapter adapter;
    private final List<ParticipantEntry> master = new ArrayList<>();  // All participants
    private final List<ParticipantEntry> visible = new ArrayList<>(); // Filtered participants

    private ParticipantEntry.Status currentFilter = ParticipantEntry.Status.ACCEPT; // Default filter
    private DatabaseWorker db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view_participant);

        spinnerFilter = findViewById(R.id.participant_filter_dropdown);
        participantListView = findViewById(R.id.participantList);

        // Set up dropdown menu — only three options (Accept / Waiting list / Cancelled)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Accept", "Waiting list", "Cancelled"}
        );
        spinnerFilter.setAdapter(spinnerAdapter);

        adapter = new ParticipantAdapter(this, (ArrayList<ParticipantEntry>) visible);
        participantListView.setAdapter(adapter);

        // Get eventId from the previous Activity
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "Missing eventId", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle dropdown filter changes
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: currentFilter = ParticipantEntry.Status.ACCEPT; break;
                    case 1: currentFilter = ParticipantEntry.Status.WAITING; break;
                    default: currentFilter = ParticipantEntry.Status.CANCELLED;
                }
                applyFilterAndRefresh();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Subscribe to Firebase real-time data updates
        db = new DatabaseWorker();
        subscribeParticipants(eventId);
    }

    // Listen to the participants of this event in Firebase
    private void subscribeParticipants(@NonNull String eventId) {
        db.getParticipants(eventId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                master.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    // child: /events/{eventId}/participants/{userId}
                    String userId = child.getKey();

                    String name = safeGet(child, "name");
                    String email = safeGet(child, "email");
                    String statusRaw = safeGet(child, "status");
                    long enrolledAt = 0L;
                    try {
                        Long t = child.child("enrolledAt").getValue(Long.class);
                        if (t != null) enrolledAt = t;
                    } catch (Exception ignored) {}

                    ParticipantEntry.Status status = ParticipantEntry.Status.from(statusRaw);

                    master.add(new ParticipantEntry(userId, name, email, enrolledAt, status));
                }
                applyFilterAndRefresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrganizerViewParticipant.this,
                        "Failed to load participants: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Apply current filter and refresh the ListView
    private void applyFilterAndRefresh() {
        visible.clear();
        for (ParticipantEntry p : master) {
            if (p.getStatus() == currentFilter) {
                visible.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Safely retrieve String value from a DataSnapshot (to prevent null pointer exceptions)
    private static String safeGet(@NonNull DataSnapshot node, @NonNull String key) {
        try {
            String v = node.child(key).getValue(String.class);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }
}

