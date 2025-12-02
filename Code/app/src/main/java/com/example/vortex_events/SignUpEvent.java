package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * Activity that handles signing up for an event or leaving an already joined event.
 */
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

    ImageButton backButton;
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

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        dbWork.getEventByID(EventID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                title = documentSnapshot.getString("name");
                location = documentSnapshot.getString("location");
                time = documentSnapshot.getDate("start_time");
                regLimit = documentSnapshot.getDate("enrollement_end");



                SignUpName.setText(title);
                SignUpTime.setText(time.toString());
                SignUpLocation.setText(location);
                lotteryWarning.setText("You will be randomly added to a lottery where the winners will be given a spot to the event on " + regLimit.toString());

                if (!alreadyRegistered) {
                    sign_up.setText("Confirm Sign Up");
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
                    sign_up.setText("Keep current registration");
                    sign_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });

                    cancel.setText("Leave event");
                    cancel.setOnClickListener(new View.OnClickListener() {
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Updates the current user's list of signed-up events after they join an event.
     *
     * This method:
     * - Retrieves the user by device ID
     * - Adds the event ID to their signed-up events
     * - Writes the updated user back to the database
     * - Navigates to the main activity with a success or failure toast
     *
     * @param eventID the ID of the event the user signed up for
     */
    private void updateUserEvents(String eventID) {
        dbWork.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
            if (user != null) {

                user.addSignedUpEvent(eventID);

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

    /**
     * Handles the logic for a user leaving an event.
     *
     * This method:
     * - Loads the user by device ID
     * - Loads the event by ID from Firestore
     * - Converts the event document into an Event object
     * - Calls RegisteredUser.leaveEvent to remove the user from event lists
     * - Shows success or failure toasts and navigates back to event details
     *
     * @param eventID the ID of the event the user is leaving
     */
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

    /**
     * Navigates back to the EventDetails screen for the given event ID.
     *
     * @param eventID the ID of the event to show details for
     */
    private void goBackToEventDetails(String eventID) {
        Intent intent = new Intent(SignUpEvent.this, EventDetails.class);
        intent.putExtra("EventID", eventID);
        startActivity(intent);
    }

}
