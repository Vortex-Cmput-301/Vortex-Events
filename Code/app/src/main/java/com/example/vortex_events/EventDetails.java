package com.example.vortex_events;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.zxing.WriterException;

import com.bumptech.glide.Glide;
import android.app.AlertDialog;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * Activity that displays detailed information about a single event and
 * allows the user to sign up, accept/decline invitations, or (if organizer)
 * edit/delete the event.
 */
public class EventDetails extends AppCompatActivity {
    String EventID;
    FirebaseFirestore db;
    DatabaseWorker dbWorker;
    Event event;
    TextView eventTitle;
    String title;
    TextView eventCapacity;
    int capacity;

    ImageView eventPoster;

    TextView eventTime;
    Date time;
    TextView eventRegLimit;
    Date regLimit;
    TextView eventLocation;
    String location;
    TextView eventDesc;
    String description;
    String orgID;
    String deviceID;

    String image;

    Button signupButton;
    Button editEventButton;
    Button notifcationsDashBoardButton;
    ImageButton moreButton;
    RegisteredUser currentUser;

    ImageView posterPreview;

    ImageView qrImage;

    ArrayList<String> wonLottery;

    ArrayList<String> waitList;

    ArrayList<String> accepted;

    ArrayList<String> declined;




    @SuppressLint("HardwareIds")
    /**
     * Activity lifecycle entry point - initializes UI and loads event data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);

        Intent returnedID = getIntent();
        EventID = returnedID.getStringExtra("EventID").toString();
        String prevActivity = returnedID.getStringExtra("prev_activity");
        if (prevActivity == null) prevActivity = "home";  // or "home"


        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();
        dbWorker = new DatabaseWorker(db);

//      Set the ui elements
        eventPoster = findViewById(R.id.iv_upload_icon);
        posterPreview = findViewById(R.id.iv_poster_preview);
        eventTitle = findViewById(R.id.event_details_title);
        eventDesc  = findViewById(R.id.event_details_desc);
        eventCapacity = findViewById(R.id.event_details_capacity);
        eventLocation = findViewById(R.id.event_details_location);
        eventRegLimit = findViewById(R.id.event_details_reg_period);
        eventTime = findViewById(R.id.event_details_time);

        signupButton = findViewById(R.id.btn_details_sign_up);
        editEventButton = findViewById(R.id.edit_event_button);
        notifcationsDashBoardButton = findViewById(R.id.organizer_notifications_button);
        Button mapButton = findViewById(R.id.btn_details_open_map);
        Button decline = findViewById(R.id.decline);
        Button accept = findViewById(R.id.accept);
        mapButton.setVisibility(View.GONE);

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        moreButton = findViewById(R.id.btn_more);
        if (moreButton != null) { //hide by default
            moreButton.setVisibility(View.GONE);
            moreButton.setEnabled(false);
        }
        dbWorker.getUserByDeviceID(deviceID).addOnSuccessListener(user -> {
            currentUser = user;
            if (moreButton != null && currentUser != null && "Admin".equals(currentUser.type)) {
                moreButton.setVisibility(View.VISIBLE);
                moreButton.setEnabled(true);
            }
        }).addOnFailureListener(e -> {
            Log.e("EventDetails", "Failed to load current user", e);
        });

        qrImage = findViewById(R.id.event_details_qr);




        qrImage = findViewById(R.id.event_details_qr);



//      Get event info
        dbWorker.getEventByID(EventID).addOnSuccessListener(documentSnapshot -> { // Note: singular
            if (documentSnapshot.exists()) {
                Log.d("OrganizerViewParticipant", "Event 'accepted' field: " + documentSnapshot.get("accepted"));
                Event event = dbWorker.convertDocumentToEvent(documentSnapshot);//use method in DatabaseWorker instead
                assert event != null;
                description = event.getDescription();
                title = event.getName();
                capacity =  event.getCapacity();
                location = event.getLocation();
                regLimit = event.getEnrollement_end();
                time = event.getStart_time();
                orgID = event.getOrganizer();
                image = event.getImage();
                wonLottery =  new ArrayList<>();
                if (event.getWonLottery()!= null){
                    wonLottery = event.getWonLottery();
                }

                waitList = event.getWaitlist();
                declined = event.getDeclined();
                accepted = event.getAccepted();


                if (image != null && !image.isEmpty()) {
                    try {

                        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);


                        Glide.with(this).load(imageBytes).into(posterPreview);
                        eventPoster.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Log.e("EventDetails", "Failed to load image", e);
                    }
                }



                eventTitle.setText(title);
                eventDesc.setText("Description: " + description);
                eventCapacity.setText("Capacity: " + capacity);
                eventLocation.setText("Location: " + location);
                eventTime.setText("Time: " + time.toString());
                eventRegLimit.setText("Registration ends: " + regLimit.toString());




                //displays QR code
                String payload = "vortex://event/" + EventID;

                try {
                    Bitmap bmp = QRCodeGenerator.generateQRCodeBitmap(payload, 600, 600);
                    qrImage.setImageBitmap(bmp);
                } catch (WriterException e) {
                    Log.e("EventDetails", "Failed to generate QR", e);
                }





                //displays QR code
                 payload = "vortex://event/" + EventID;

                try {
                    Bitmap bmp = QRCodeGenerator.generateQRCodeBitmap(payload, 600, 600);
                    qrImage.setImageBitmap(bmp);
                } catch (WriterException e) {
                    Log.e("EventDetails", "Failed to generate QR", e);
                }

                Log.d("LotteryDebug", "--------------------------------------------------");
                Log.d("LotteryDebug", "My Device ID: " + deviceID);
                Log.d("LotteryDebug", "Waitlist: " + waitList.toString());
                Log.d("LotteryDebug", "Won Lottery List: " + wonLottery.toString());

                boolean isInWaitlist = waitList.contains(deviceID);
                boolean isInWonList = wonLottery.contains(deviceID);

                Log.d("LotteryDebug", "In Waitlist: " + isInWaitlist);
                Log.d("LotteryDebug", "In wonlist: " + isInWonList);
                Log.d("LotteryDebug", "--------------------------------------------------");



// if the event is owned by the current device id
                if (orgID.equals(deviceID)){
                    signupButton.setText("Edit Events");
                    editEventButton.setVisibility(VISIBLE);
                    mapButton.setVisibility(VISIBLE);
                    notifcationsDashBoardButton.setVisibility(VISIBLE);

//                    Listener for edit details intents

                    signupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           Intent intent = new Intent(EventDetails.this, EditEvents.class);
                          intent.putExtra("EventID", EventID);
                          startActivity(intent);
                        }
                    });

                    editEventButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EventDetails.this, OrganizerViewParticipant.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });

                    notifcationsDashBoardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EventDetails.this, OrganizerNotificationsDashboard.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });

                    mapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(EventDetails.this, MapEntrants.class);
                            intent.putExtra("EventID", EventID);
                            startActivity(intent);
                        }
                    });




                }else{
                    if (waitList.contains(deviceID)) {
                        signupButton.setText("Registered, leave or edit");
//                    Listener for sign up for event
                        signupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            //Todo: link to edit page with leave button --Kehan 11.16
                            public void onClick(View view) {
                                Intent intent = new Intent(EventDetails.this, SignUpEvent.class);
                                intent.putExtra("EventID", EventID);
                                intent.putExtra("alreadyRegistered", true);
                                startActivity(intent);
                            }
                        });

                    }else if (wonLottery.contains(deviceID)){
                        signupButton.setVisibility(View.GONE);
                        accept.setVisibility(VISIBLE);
                        decline.setVisibility(VISIBLE);

                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                accepted.add(deviceID);
                                wonLottery.remove(deviceID);


                                db.collection("Events").document(EventID)
                                        .update("accepted", accepted, "wonLottery", wonLottery)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EventDetails.this, "Invitation Accepted!", Toast.LENGTH_SHORT).show();

                                            // Update UI
                                            accept.setVisibility(View.GONE);
                                            decline.setVisibility(View.GONE);
                                            signupButton.setText("Registered (Accepted)");
                                            signupButton.setVisibility(View.VISIBLE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EventDetails.this, "Error: Could not save.", Toast.LENGTH_SHORT).show();
                                            Log.e("EventDetails", "Accept failed", e);
                                        });
                            }
                        });


                        decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                declined.add(deviceID);
                                wonLottery.remove(deviceID);


                                db.collection("Events").document(EventID)
                                        .update("declined", declined, "wonLottery", wonLottery ).addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EventDetails.this, "Invitation Declined", Toast.LENGTH_SHORT).show();

                                            accept.setVisibility(View.GONE);
                                            decline.setVisibility(View.GONE);
                                            signupButton.setText("Declined");
                                            signupButton.setVisibility(View.VISIBLE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EventDetails.this, "Error: Could not save.", Toast.LENGTH_SHORT).show();
                                            Log.e("EventDetails", "Decline failed", e);
                                        });
                            }
                        });


                    } else if (declined.contains(deviceID) || accepted.contains(deviceID)) {
                        //Print Has accepted or has declined
                        if(declined.contains(deviceID)){
                            signupButton.setText("Invitation Declined");
                        }else {
                            signupButton.setText("Invitation Accepted");
                        }


                    } else{
                        signupButton.setText("Sign up for this event");
//                    Listener for sign up for event
                        signupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(EventDetails.this, SignUpEvent.class);
                                intent.putExtra("EventID", EventID);
                                intent.putExtra("alreadyRegistered", false);
                                startActivity(intent);
                            }
                        });
                    }
                }
                if (moreButton != null) {
                    moreButton.setOnClickListener(view -> {
                        // Safety check: allow only Admin to perform delete
                        if (currentUser == null || !"Admin".equals(currentUser.type)) {
                            Log.w("EventDetails", "Delete action blocked: current user is not admin");
                            return;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
                        builder.setTitle("Delete event")
                                .setMessage("Are you sure you want to delete this event?")
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    if (event == null) {
                                        Log.e("EventDetails", "Event is null, cannot delete");
                                        return;
                                    }
                                    dbWorker.deleteEvent(event)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("EventDetails", "Event deleted successfully");
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("EventDetails", "Failed to delete event", e);
                                            });
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }

            } else {
                Log.e("OrganizerViewParticipant", "No such document found with that ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("OrganizerViewParticipant", "Error getting document", e);
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        assert prevActivity != null;
        switch (prevActivity) {
            case "home":
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                break;
            case "explore":
                bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                break;
            case "create":
                bottomNavigationView.setSelectedItemId(R.id.nav_create);
                break;
            case "search":
                bottomNavigationView.setSelectedItemId(R.id.nav_search);
                break;
            case "scan":
                bottomNavigationView.setSelectedItemId(R.id.nav_scan_qr);
                break;
        }
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
                    Intent intent = new Intent(getApplicationContext(), SearchEvents.class);
                    startActivity(intent);
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
    /**
     * Move the given user ID from the event waitlist into the declined list
     * in Firestore.
     * @param userID device/user id to move to declined
     */
    public void switchToDeclined(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .document(EventID)
                .update("waitlist", FieldValue.arrayRemove(userID), "declined", FieldValue.arrayUnion(userID));
    }
}