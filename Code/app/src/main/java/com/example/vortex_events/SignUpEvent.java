package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignUpEvent extends AppCompatActivity {
    String EventID;
    Date time;
    String title;
    Date regLimit;
    String location;

    TextView SignUpName;
    TextView SignUpLocation;
    TextView SignUpTime;
    TextView lotteryWarning;
    Button cancel;
    Button sign_up;
    DatabaseWorker dbWork;
    FirebaseFirestore db;
    String deviceID;

    String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_event);

        Intent returnedID = getIntent();
        EventID = returnedID.getStringExtra("EventID").toString();

        dbWork = new DatabaseWorker();


        SignUpName = findViewById(R.id.sign_up_name);
        SignUpLocation = findViewById(R.id.sign_up_location);
        SignUpTime = findViewById(R.id.sign_up_date);
        lotteryWarning = findViewById(R.id.sign_up_warning);

        cancel = findViewById(R.id.sign_up_cancel);
        sign_up = findViewById(R.id.sign_up_sign_up);

        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);



        dbWork.getEventByID(EventID).addOnSuccessListener(documentSnapshot -> { // Note: singular
            if (documentSnapshot.exists()) {
                title = documentSnapshot.getString("name");
                location = documentSnapshot.getString("location");
                time = documentSnapshot.getDate("start_time");
                regLimit = documentSnapshot.getDate("enrollement_end");


                //        Update UI
                SignUpName.setText(title);
                SignUpTime.setText(time.toString());
                SignUpLocation.setText(location);
                lotteryWarning.setText("You will be randomly added to a lottery where the winners will be given a spot to the event on " + regLimit.toString());

                sign_up.setText("Confirm Sign Up");
                sign_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Waitlist logic
                        dbWork.getEventByID(EventID).addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<String> waitlist = (ArrayList<String>) documentSnapshot.get("waitlist");
                                waitlist.add(deviceID);

                                dbWork.updateWaitlist(waitlist, EventID).addOnSuccessListener(aVoid -> {
                                    // After updating waitlist, update user's signed_up_events
                                    updateUserEvents(EventID);
                                }).addOnFailureListener(e -> {
                                    Log.e("SignUpEvent", "DB error updating waitlist");
                                });
                            } else {
                                Log.e("SignUpEvent", "Device ID " + deviceID + "NOWHERE TO BE FOUND");
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("SignUpEvent", "DB error getting event");
                        });
                    }
                });


                cancel.setText("Cancel Sign Up");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
                        intent.putExtra("EventID", EventID);
                        startActivity(intent);
                    }
                });

            } else {
                Log.e("SignUpEvent", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "Error getting document", e);
        });

        // Check if user is guest (original logic)
        dbWork.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
            if (user != null && user.getType().equals("Guest")) {
                Toast.makeText(SignUpEvent.this, "Guests can't sign up for events", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
                intent.putExtra("EventID", EventID);
                startActivity(intent);
                finish(); // Close activity for guest users
            }
        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "DB error getting user type");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void updateUserEvents(String eventID) {
        dbWork.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
            if (user != null) {
                // Use the convenience method
                user.addSignedUpEvent(eventID);

                // Update user in database
                dbWork.updateUser(user).addOnSuccessListener(aVoid -> {
                    Log.d("SignUpEvent", "User events updated successfully");
                    Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Log.e("SignUpEvent", "DB error updating user");
                    Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                    startActivity(intent);
                });
            } else {
                Log.e("SignUpEvent", "User not found");
                Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "DB error getting user");
            Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
            startActivity(intent);
        });
    }

}
