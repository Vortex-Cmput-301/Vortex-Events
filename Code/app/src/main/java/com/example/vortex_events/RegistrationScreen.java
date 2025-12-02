package com.example.vortex_events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;



/**
 * Activity that handles user registration in the Vortex Events app.
 * After submitting the form, the user is redirected to the main activity.
 */
public class RegistrationScreen extends AppCompatActivity {
    EditText phoneField;
    EditText emailField;
    EditText nameField;
    Button signUpButton;

    String phoneNumber;
    String emailAddress;
    String userName;

    FirebaseFirestore db;

    DatabaseWorker dbWorker;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;


    /**
     * Launcher that requests both fine and coarse location permissions.
     * If granted, the user's last known location is retrieved.
     */
    private ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
        if (fineLocationGranted != null && fineLocationGranted) {
            // Precise location access granted.
            getLocation();
        } else if (coarseLocationGranted != null && coarseLocationGranted) {
            // Only approximate location access granted.
            getLocation();
        } else {
            // No location access granted.
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        db = FirebaseFirestore.getInstance();
        dbWorker = new DatabaseWorker(db);

        phoneField = findViewById(R.id.phone_field);
        emailField = findViewById(R.id.email_field);
        nameField = findViewById(R.id.name_field_sign_up);
        signUpButton = findViewById(R.id.sign_up_submit);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = phoneField.getText().toString();
                emailAddress = emailField.getText().toString();
                userName = nameField.getText().toString();

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                android.util.Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            android.util.Log.d("FCM", "Token: " + token);
                            RegisteredUser user =  new RegisteredUser( RegistrationScreen.this, phoneNumber, emailAddress, userName, token, longitude, latitude, true);
                            user.setType("Registered User");
                            dbWorker.createRegisteredUser(user);
                        });





                Intent intent = new Intent(RegistrationScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }


    /**
     * Retrieves the last known device location using the FusedLocationProviderClient.
     * This method is only called when location permissions have already been granted.
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
    }
}