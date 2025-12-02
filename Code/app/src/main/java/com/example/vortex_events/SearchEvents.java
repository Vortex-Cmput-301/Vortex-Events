package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that provides a simple search UI to filter events by tags.
 */
public class SearchEvents extends AppCompatActivity {

    /**
     * Initialize search UI and wire text change listeners to filter events.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_events);

        DatabaseWorker db = new DatabaseWorker();
        EditText searchBar = findViewById(R.id.searchBar);
        final String[] stringToFilterOut = {"seeyuh"};
        List<String> filteredEvents = new ArrayList<>();
        ArrayAdapter<String> searchResults = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                filteredEvents);
        ListView listView = findViewById(R.id.searchResults);
        listView.setAdapter(searchResults);

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stringToFilterOut[0] = s.toString();
                db.getAllEvents().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        filteredEvents.clear();

                        List<Event> allEvents = task.getResult().toObjects(Event.class);

                        for (Event event : allEvents) {

                            // Add a null check before calling .contains()
                            if (event.getTags() != null) {
                                for (String tag : event.getTags()) {
                                    if (tag.toLowerCase().contains(stringToFilterOut[0].toLowerCase())) {
                                        filteredEvents.add(event.getName());
                                        break; // Stop checking other tags once we found a match
                                    }
                                }
                            }
                        }
                        searchResults.notifyDataSetChanged();

                        Log.d("Firestore", "Filtered events count: " + filteredEvents.size());

                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

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
}