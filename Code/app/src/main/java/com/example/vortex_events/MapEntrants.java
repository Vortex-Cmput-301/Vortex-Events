package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vortex_events.databinding.ActivityMapEntrantsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Activity that displays entrant locations on a Google Map for a specific event.
 * Accepted entrants shown in green, waitlist in yellow, declined in red.
 */
public class MapEntrants extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapEntrantsBinding binding;

    DatabaseWorker dbWorker;
    ArrayList<String> acceptedList = new ArrayList<>();
    ArrayList<String> waitlistList = new ArrayList<>();
    ArrayList<String> deletedList = new ArrayList<>();


    /**
     * Initialize the activity, load event data, and prepare the map.
     * @param savedInstanceState saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapEntrantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView backButton = findViewById(R.id.button_back);
        backButton.setColorFilter(Color.WHITE);
        backButton.setOnClickListener(v -> {
            finish();
        });


        Intent returnedID = getIntent();
        String eventID = returnedID.getStringExtra("EventID").toString();

        dbWorker = new DatabaseWorker();

        dbWorker.getEventByID(eventID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("OrganizerViewParticipant", "Event 'accepted' field: " +
                        documentSnapshot.get("accepted"));
                acceptedList.addAll((ArrayList<String>) documentSnapshot.get("accepted"));
                waitlistList.addAll((ArrayList<String>) documentSnapshot.get("waitlist"));
                deletedList.addAll((ArrayList<String>) documentSnapshot.get("declined"));
                addToMap();
            } else {
                Log.e("OrganizerViewParticipant", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("OrganizerViewParticipant", "Error getting document", e);
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Add markers to the map for accepted (green), waitlist (yellow), and declined (red) users.
     */
    public void addToMap() {
        for (String userID : acceptedList) {
            dbWorker.getUserByDeviceID(userID).addOnSuccessListener(user -> {
                if (user != null) {
                    double latitude = user.getLatitude();
                    double longitude = user.getLongitude();

                    LatLng userLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions()
                                    .position(userLocation)
                                    .title(user.getName()))
                            .setIcon(BitmapDescriptorFactory
                                    .defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN));
                }
            });

        }

        for (String userID : waitlistList) {
            dbWorker.getUserByDeviceID(userID).addOnSuccessListener(user -> {
                if (user != null) {
                    double latitude = user.getLatitude();
                    double longitude = user.getLongitude();

                    LatLng userLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions()
                                    .position(userLocation)
                                    .title(user.getName()))
                            .setIcon(BitmapDescriptorFactory
                                    .defaultMarker(
                                            BitmapDescriptorFactory.HUE_YELLOW));
                }
            });

        }

        for (String userID : deletedList) {
            dbWorker.getUserByDeviceID(userID).addOnSuccessListener(user -> {
                if (user != null) {
                    double latitude = user.getLatitude();
                    double longitude = user.getLongitude();


                    LatLng userLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions()
                                    .position(userLocation)
                                    .title(user.getName()))
                            .setIcon(BitmapDescriptorFactory
                                    .defaultMarker(
                                            BitmapDescriptorFactory.HUE_RED));
                }
            });

        }

    }

    /**
     * Called when the Google Map is ready. Sets up the map and centers it on Edmonton.
     * @param googleMap the GoogleMap object to manipulate
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng edmonton = new LatLng(53.5462, -113.4937);
        CameraUpdate newPositionAndZoom = CameraUpdateFactory.newLatLngZoom(edmonton, 11.0f);
        googleMap.moveCamera(newPositionAndZoom);
    }


}