package com.example.vortex_events;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.zxing.WriterException;

import com.bumptech.glide.Glide;

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
    ImageView posterPreview;
    Button notifcationsDashBoardButton;

    ImageView qrImage;


    @SuppressLint("HardwareIds")
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

//      Set the ui elemts
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

        qrImage = findViewById(R.id.event_details_qr);

        Button mapButton = findViewById(R.id.btn_details_open_map);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, EntrantsMap.class);
                intent.putExtra("EventID", EventID);
                startActivity(intent);
            }
        });

        Button mapButton = findViewById(R.id.btn_details_open_map);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, EntrantsMap.class);
                intent.putExtra("EventID", EventID);
                startActivity(intent);
            }
        });

        qrImage = findViewById(R.id.event_details_qr);



//      Get event info
        dbWorker.getEventByID(EventID).addOnSuccessListener(documentSnapshot -> { // Note: singular
            if (documentSnapshot.exists()) {
                Log.d("OrganizerViewParticipant", "Event 'accepted' field: " + documentSnapshot.get("accepted"));
                Event event = dbWorker.convertDocumentToEvent(documentSnapshot);//use method in DatabaseWorker instead
                description = event.getDescription();
                title = event.getName();
                capacity =  event.getCapacity();
                location = event.getLocation();
                regLimit = event.getEnrollement_end();
                time = event.getStart_time();
                orgID = event.getOrganizer();
                image = event.getImage();

                if (image != null && !image.isEmpty()) {
                    try {

                        byte[] imageBytes = android.util.Base64.decode(image, android.util.Base64.DEFAULT);


                        Glide.with(this).load(imageBytes).into(posterPreview);
                        eventPoster.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Log.e("EventDetails", "Failed to load image", e);
                    }
                } else {
                    eventPoster.setVisibility(View.GONE);
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
                String payload = "vortex://event/" + EventID;

                try {
                    Bitmap bmp = QRCodeGenerator.generateQRCodeBitmap(payload, 600, 600);
                    qrImage.setImageBitmap(bmp);
                } catch (WriterException e) {
                    Log.e("EventDetails", "Failed to generate QR", e);
                }



// if the event is owned by the current device id
                if (orgID.equals(deviceID)){
                    signupButton.setText("Edit Events");
                    editEventButton.setVisibility(VISIBLE);
                    mapButton.setVisibility(VISIBLE);
                    notifcationsDashBoardButton.setVisibility(VISIBLE);

//                    Listeneer for edit details intents

                    signupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(EventDetails.this, EditEvents.class);
//                            intent.putExtra("EventID", EventID);
//                            startActivity(intent);
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
                    if (event.getWaitlist().contains(deviceID)) {
                        signupButton.setText("Registered, leave or edit");
//                    Listener for sign up for event
                        signupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            //Todo: link to edit page with leave button --Kehan 11.16
                            public void onClick(View view) {
                                Intent intent = new Intent(EventDetails.this, SignUpEvent.class);
                                intent.putExtra("EventID", EventID);
                                startActivity(intent);
                            }
                        });
                    }else{
                        signupButton.setText("Sign up for this event");
//                    Listener for sign up for event
                        signupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(EventDetails.this, SignUpEvent.class);
                                intent.putExtra("EventID", EventID);
                                startActivity(intent);
                            }
                        });
                    }
                }


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
}