package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.checkerframework.checker.units.qual.Time;
import org.checkerframework.common.returnsreceiver.qual.This;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;





/**
 * Activity for creating a new event.
 * Allows a registered user to fill out a form and submit a new event to the database.
 */
public class CreateActivityEvents extends AppCompatActivity {

    /**
     * Handles database read/write operations for Events and Users.
     */
    private DatabaseWorker dbWorker;
    /**
     * Handles setting waitlist limits.
     */
    private WaitlistManager waitlistManager;
    /**
     * Utility for generating and reversing event IDs.
     */
    private HashWorker hashWorker;
    /**
     * The unique Android device ID of the current user.
     */
    private String currentDeviceID;

    private android.net.Uri imageUri; // Stores the image path on the phone
    private android.widget.ImageView posterPreview;

    private androidx.activity.result.ActivityResultLauncher<String> selectImageLauncher; //Opens gallery


    /**
     * Displays a DatePickerDialog followed by a TimePickerDialog.
     * The result is formatted and set as the text of the provided EditText
     *
     * @param fieldToUpdate The EditText field that will receive the formatted date/time string.
     */
    private void showDateTimePickerDialog(EditText fieldToUpdate) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar timeCalendar = Calendar.getInstance();
                    int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = timeCalendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            CreateActivityEvents.this,
                            (view1, hourOfDay, minute1) -> {
                                Calendar selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(year1, monthOfYear, dayOfMonth, hourOfDay, minute1);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
                                String formattedDate = sdf.format(selectedDateTime.getTime());
                                fieldToUpdate.setText(formattedDate);
                            },
                            hour, minute, true); // true = 24-hour time
                    timePickerDialog.show();
                },
                year, month, day);
        datePickerDialog.show();
    }


    /**
     * Called when the activity is first created.
     * Initializes the UI, sets up listeners, and handles the event creation logic.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle).
     */
    @SuppressLint("HardwareIds") // Suppress warning for device ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectImageLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        // Show the selected image on screen
                        ((android.widget.ImageView) findViewById(R.id.iv_upload_icon)).setImageURI(uri);
                        // Hide the upload icon so the image is visible
                        findViewById(R.id.iv_upload_icon).setVisibility(View.GONE);
                    }
                }
        );
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_events);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helpers and get device ID
        dbWorker = new DatabaseWorker();
        waitlistManager = new WaitlistManager();
        hashWorker = new HashWorker();
        currentDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // --- Date/Time Picker Setup( Dont touch pls)
        View.OnClickListener dateTimePickerListener = v -> showDateTimePickerDialog((EditText) v);

        // Find all date/time EditText fields using their correct IDs
        EditText eventStartTimeField = findViewById(R.id.et_event_start_time);
        EditText eventEndTimeField = findViewById(R.id.et_event_end_time);
        EditText enrollStartTimeField = findViewById(R.id.et_registration_period_start);
        EditText enrollEndTimeField = findViewById(R.id.et_registration_period_end);

        // Apply the listener to all date/time fields
        eventStartTimeField.setOnClickListener(dateTimePickerListener);
        eventEndTimeField.setOnClickListener(dateTimePickerListener);
        enrollStartTimeField.setOnClickListener(dateTimePickerListener);
        enrollEndTimeField.setOnClickListener(dateTimePickerListener);
        findViewById(R.id.fl_poster_upload).setOnClickListener(v -> {
            selectImageLauncher.launch("image/*"); // Open gallery for images only
        });

        //Submit Button
        Button submitButton = findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(v -> {

            // Get all text from form fields
            String eventName = ((EditText) findViewById(R.id.et_event_name)).getText().toString();
            String eventLocation = ((EditText) findViewById(R.id.et_location)).getText().toString();
            String description = ((EditText) findViewById(R.id.et_description)).getText().toString();
            String tagString = ((EditText) findViewById(R.id.et_tag)).getText().toString();
            String capacityString = ((EditText) findViewById(R.id.et_capacity)).getText().toString();
            String waitingListString = ((EditText) findViewById(R.id.et_waiting_list_limit)).getText().toString();
            String eventStartString = ((EditText) findViewById(R.id.et_event_start_time)).getText().toString();
            String eventEndString = ((EditText) findViewById(R.id.et_event_end_time)).getText().toString();
            //IDK why this is giving a warning, works so dont touch

            // Get text using the correct IDs
            String enrollStartString = ((EditText) findViewById(R.id.et_registration_period_start)).getText().toString();
            String enrollEndString = ((EditText) findViewById(R.id.et_registration_period_end)).getText().toString();

            // Validation: Check if any field is empty
            if (TextUtils.isEmpty(eventName) ||
                    TextUtils.isEmpty(eventLocation) ||
                    TextUtils.isEmpty(description) ||
                    TextUtils.isEmpty(tagString) ||
                    TextUtils.isEmpty(capacityString) ||
                    TextUtils.isEmpty(waitingListString) ||
                    TextUtils.isEmpty(eventStartString) ||
                    TextUtils.isEmpty(eventEndString) ||
                    TextUtils.isEmpty(enrollStartString) ||
                    TextUtils.isEmpty(enrollEndString)) {

                Toast.makeText(CreateActivityEvents.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Stop execution
            }

            // Parse numerical and boolean fields
            int capacity;
            int waitingListLimit;
            try {
                capacity = Integer.parseInt(capacityString);
                waitingListLimit = Integer.parseInt(waitingListString);
            } catch (NumberFormatException e) {
                Log.e("FormData", "Failed to parse a number", e);
                Toast.makeText(CreateActivityEvents.this, "Capacity must be a valid number", Toast.LENGTH_SHORT).show();
                return; // Stop execution
            }

            SwitchMaterial geoSwitch = findViewById(R.id.switch_geolocation);
            boolean geolocationRequirement = geoSwitch.isChecked();


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
            Date eventStartTime;
            Date eventEndTime;
            Date enrollmentStartTime;
            Date enrollmentEndTime;
            try {
                eventStartTime = sdf.parse(eventStartString);
                eventEndTime = sdf.parse(eventEndString);
                enrollmentStartTime = sdf.parse(enrollStartString); // Correct variable
                enrollmentEndTime = sdf.parse(enrollEndString); // Correct variable
            } catch (ParseException e) {
                Log.e("FormData", "An impossible error occurred while parsing dates!", e);
                Toast.makeText(CreateActivityEvents.this, "A critical error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                return; // Stop execution
            }

            // Parse tag string into a list
            ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagString.split(" ")));

            String imageString = encodeImage(imageUri); // Call the helper function

            if (imageString == null) {
                Toast.makeText(CreateActivityEvents.this, "Image is too large or invalid.", Toast.LENGTH_SHORT).show();
                return;
            }



            //check if the user exists
            dbWorker.getUserByDeviceID(currentDeviceID).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null) {
                    //Toast.makeText(CreateActivityEvents.this, "Reading User profile", Toast.LENGTH_SHORT).show();
                    // User exists, cast them to RegisteredUser
                    RegisteredUser currentUser = documentSnapshot;
                    if (currentUser == null) {
                        Toast.makeText(CreateActivityEvents.this, "Failed to read user profile.", Toast.LENGTH_SHORT).show();
                        return;
                    }




                    // Build the Event object
                    Event event = new Event(
                            eventName,
                            eventLocation,
                            currentUser.deviceID, // Use real user ID
                            hashWorker.generateEventID(eventName, currentUser.deviceID), // Use real event ID
                            enrollmentStartTime,
                            enrollmentEndTime,
                            eventStartTime,
                            eventEndTime,
                            tagsList,
                            description,
                            capacity
                    );

                    event.setImage(imageString);




                    // Create the event in the database
                    dbWorker.createEvent(currentUser, event).addOnSuccessListener(aVoid -> {
                        Log.d("FormData", "Event document created.");

                        Toast.makeText(CreateActivityEvents.this, "Creating Event", Toast.LENGTH_SHORT).show();


                        waitlistManager.setWaitlistLimit(event.getEventID(), waitingListLimit).addOnSuccessListener(aVoid1 -> {

                            // FINAL SUCCESS
                            Toast.makeText(CreateActivityEvents.this, "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish(); // Close this activity

                        }).addOnFailureListener(e -> {
                            Log.e("FormData", "Event created, but failed to set waitlist limit", e);
                            Toast.makeText(CreateActivityEvents.this, "Event created (limit error)", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    }).addOnFailureListener(e -> {
                        Log.e("FormData", "Failed to create event.", e);
                        Toast.makeText(CreateActivityEvents.this, "Failed to create event.", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    // User does not exist, block event creation
                    Toast.makeText(CreateActivityEvents.this, "User not found. Cannot create event.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("FormData", "Error checking user.", e);
                Toast.makeText(CreateActivityEvents.this, "Error checking user.", Toast.LENGTH_SHORT).show();
            });
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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

    private String encodeImage(Uri imageUri) {
        try {

            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            //Calculate new size
            int maxWidth = 800;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            //resize if the image is actually bigger than 800px
            if (width > maxWidth) {
                float ratio = (float) width / maxWidth;
                width = maxWidth;
                height = (int) (height / ratio);
            }

            //resized bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);

            //Compress the resized image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] bytes = stream.toByteArray();

            //Check if its bigger, compress again
            if (bytes.length > 800000) {
                stream.reset(); // Clear stream
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream); // Drop to 30%
                bytes = stream.toByteArray();
            }

            //Check if its bigger, compress again just incase
            if (bytes.length > 1000000) {
                Toast.makeText(this, "Image is still too large. Please pick another.", Toast.LENGTH_SHORT).show();
                return null;
            }

            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}