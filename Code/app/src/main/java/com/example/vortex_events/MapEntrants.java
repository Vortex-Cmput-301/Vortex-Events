package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vortex_events.databinding.ActivityMapEntrantsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapEntrants extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapEntrantsDebug"; // Filter Logcat by this TAG
    private GoogleMap mMap;
    private ActivityMapEntrantsBinding binding;
    private DatabaseWorker dbWorker;

    private ArrayList<String> acceptedList = new ArrayList<>();
    private ArrayList<String> waitlistList = new ArrayList<>();
    private ArrayList<String> declinedList = new ArrayList<>();

    private boolean isMapReady = false;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started.");

        binding = ActivityMapEntrantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setColorFilter(Color.WHITE);
        binding.buttonBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String eventID = intent.getStringExtra("EventID");

        if (eventID == null) {
            Log.e(TAG, "onCreate: Event ID is NULL. Cannot fetch data.");
            Toast.makeText(this, "Error: Event ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "onCreate: Event ID found: " + eventID);

        dbWorker = new DatabaseWorker();
        loadEventData(eventID);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.d(TAG, "onCreate: MapFragment found, initializing async map.");
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "onCreate: MapFragment is NULL in layout.");
        }
    }

    private void loadEventData(String eventID) {
        Log.d(TAG, "loadEventData: Fetching event details from Firestore...");
        dbWorker.getEventByID(eventID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "loadEventData: Document exists.");

                List<String> accepted = (List<String>) documentSnapshot.get("accepted");
                List<String> waitlist = (List<String>) documentSnapshot.get("waitlist");
                List<String> declined = (List<String>) documentSnapshot.get("declined");

                if (accepted != null) acceptedList.addAll(accepted);
                if (waitlist != null) waitlistList.addAll(waitlist);
                if (declined != null) declinedList.addAll(declined);

                Log.d(TAG, "loadEventData: Data Lists Populated. " +
                        "Accepted: " + acceptedList.size() +
                        ", Waitlist: " + waitlistList.size() +
                        ", Declined: " + declinedList.size());

                isDataLoaded = true;
                updateMapMarkers();
            } else {
                Log.e(TAG, "loadEventData: No such document found for ID: " + eventID);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "loadEventData: Database fetch failed.", e);
        });
    }

    private void updateMapMarkers() {
        Log.d(TAG, "updateMapMarkers: Attempting to refresh map...");

        // Detailed check to see why it might fail
        if (mMap == null) {
            Log.w(TAG, "updateMapMarkers: SKIPPED. GoogleMap object is null.");
            return;
        }
        if (!isMapReady) {
            Log.w(TAG, "updateMapMarkers: SKIPPED. Map is not ready (onMapReady hasn't finished).");
            return;
        }
        if (!isDataLoaded) {
            Log.w(TAG, "updateMapMarkers: SKIPPED. Data is not loaded from Firestore yet.");
            return;
        }

        Log.d(TAG, "updateMapMarkers: CONDITIONS MET. Clearing map and adding markers.");
        mMap.clear();

        addMarkersForCategory(acceptedList, BitmapDescriptorFactory.HUE_GREEN, "Accepted");
        addMarkersForCategory(waitlistList, BitmapDescriptorFactory.HUE_YELLOW, "Waitlist");
        addMarkersForCategory(declinedList, BitmapDescriptorFactory.HUE_RED, "Declined");
    }

    private void addMarkersForCategory(ArrayList<String> userIDs, float colorHue, String categoryName) {
        if (userIDs.isEmpty()) {
            Log.d(TAG, "addMarkers: List for " + categoryName + " is empty. No markers to add.");
            return;
        }

        Log.d(TAG, "addMarkers: Processing " + userIDs.size() + " users for " + categoryName);

        for (String userID : userIDs) {
            dbWorker.getUserByDeviceID(userID).addOnSuccessListener(user -> {
                if (user == null) {
                    Log.w(TAG, "addMarkers: User object returned NULL for ID: " + userID);
                    return;
                }

                // Check if activity is still valid
                if (isFinishing() || isDestroyed()) {
                    Log.w(TAG, "addMarkers: Activity died before user data returned.");
                    return;
                }

                double longitude = user.getLatitude();
                double latitude = user.getLongitude();

                Log.d(TAG, "addMarkers: User found: " + user.getName() + " [" + latitude + ", " + longitude + "]");

                // Filter out default 0.0, 0.0 coordinates (Null Island)
                if (latitude == 0.0 && longitude == 0.0) {
                    Log.w(TAG, "addMarkers: SKIPPING " + user.getName() + " - Coordinates are default (0,0).");
                    return;
                }

                LatLng userLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title(user.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(colorHue)));

                Log.d(TAG, "addMarkers: PIN ADDED for " + user.getName());

            }).addOnFailureListener(e -> {
                Log.e(TAG, "addMarkers: Failed to fetch user: " + userID, e);
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Google Map is ready.");
        mMap = googleMap;
        isMapReady = true;

        LatLng edmonton = new LatLng(53.5462, -113.4937);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 11.0f));

        updateMapMarkers();
    }
}