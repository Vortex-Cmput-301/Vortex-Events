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
        boolean alreadyRegistered = returnedID.getBooleanExtra("alreadyRegistered", false);

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
                // *** CHANGED: behavior depends on whether user is already registered
                if (!alreadyRegistered) { // new registration
                    sign_up.setText("Confirm Sign Up"); // *** CHANGED
                    sign_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dbWork.getEventByID(EventID).addOnSuccessListener(snapshot -> {
                                if (!snapshot.exists()) {
                                    Log.e("SignUpEvent", "Event not found: " + EventID);
                                    return;
                                }
                                List<String> waitlist = (ArrayList<String>) snapshot.get("waitlist");
                                if (waitlist == null) {
                                    waitlist = new ArrayList<>();
                                }
                                if (!waitlist.contains(deviceID)) {
                                    waitlist.add(deviceID);
                                }

                                dbWork.updateWaitlist(waitlist, EventID).addOnSuccessListener(aVoid -> {
                                    updateUserEvents(EventID);
                                }).addOnFailureListener(e -> {
                                    Log.e("SignUpEvent", "Error updating waitlist", e);
                                });
                            }).addOnFailureListener(e -> {
                                Log.e("SignUpEvent", "Error loading event for signup", e);
                            });
                        }
                    });

                    cancel.setText("Cancel Sign Up"); // *** CHANGED
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });

                } else { // already registered: keep or leave
                    sign_up.setText("Keep current registration"); // *** NEW
                    sign_up.setOnClickListener(new View.OnClickListener() { // *** NEW
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });

                    cancel.setText("Leave event"); // *** NEW
                    cancel.setOnClickListener(new View.OnClickListener() { // *** NEW
                        @Override
                        public void onClick(View view) {
                            leaveEvent(EventID);
                        }
                    });
                }





            } else {
                Log.e("SignUpEvent", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "Error getting document", e);
        });

        // Check if user is guest (original logic)
//        dbWork.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
//            if (user != null && user.getType().equals("Guest")) {
//                Toast.makeText(SignUpEvent.this, "Guests can't sign up for events", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
//                intent.putExtra("EventID", EventID);
//                startActivity(intent);
//                finish(); // Close activity for guest users
//            }
//        }).addOnFailureListener(e -> {
//            Log.e("SignUpEvent", "DB error getting user type");
//        });

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
                    Toast.makeText(SignUpEvent.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Log.e("SignUpEvent", "DB error updating user");
                    Toast.makeText(SignUpEvent.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                    startActivity(intent);
                });
            } else {
                Log.e("SignUpEvent", "User not found");
                Toast.makeText(SignUpEvent.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "DB error getting user");
            Toast.makeText(SignUpEvent.this, "Sign up failed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpEvent.this, MainActivity.class);
            startActivity(intent);
        });
    }
    private void leaveEvent(String eventID) {
        dbWork.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
            if (user == null) {
                Log.e("SignUpEvent", "User not found when leaving event");
                goBackToEventDetails(eventID);
                return;
            }

            dbWork.getEventByID(eventID).addOnSuccessListener(snapshot -> {
                if (!snapshot.exists()) {
                    Log.e("SignUpEvent", "Event not found when leaving: " + eventID);
                    goBackToEventDetails(eventID);
                    return;
                }

                Event event = dbWork.convertDocumentToEvent(snapshot);
                if (event == null) {
                    Log.e("SignUpEvent", "Failed to convert event document when leaving");
                    goBackToEventDetails(eventID);
                    return;
                }

                if (event.getWaitlist() == null) {
                    event.setWaitlist(new ArrayList<String>());
                }

                RegisteredUser registeredUser = user;
                registeredUser.leaveEvent(event, dbWork, new RegisteredUser.LeaveEventCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SignUpEvent.this, "You have left this event.", Toast.LENGTH_SHORT).show();
                        goBackToEventDetails(eventID);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("SignUpEvent", "Error leaving event", e);
                        Toast.makeText(SignUpEvent.this, "Failed to leave event.", Toast.LENGTH_SHORT).show();
                        goBackToEventDetails(eventID);
                    }
                });

            }).addOnFailureListener(e -> {
                Log.e("SignUpEvent", "Error loading event when leaving", e);
                goBackToEventDetails(eventID);
            });

        }).addOnFailureListener(e -> {
            Log.e("SignUpEvent", "Error loading user when leaving", e);
            goBackToEventDetails(eventID);
        });
    }

    // *** NEW: simple helper to navigate back to details
    private void goBackToEventDetails(String eventID) {
        Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
        intent.putExtra("EventID", eventID);
        startActivity(intent);
    }

}
