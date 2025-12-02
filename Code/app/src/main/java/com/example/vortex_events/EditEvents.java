package com.example.vortex_events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.vortex_events.DatabaseWorker;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditEvents extends AppCompatActivity {

    // UI Elements
    private EditText etEventName, etLocation, etDescription, etTags;
    private EditText etCapacity, etWaitlistLimit;
    private EditText etEventStart, etEventEnd, etEnrollStart, etEnrollEnd;

    private ImageView ivPosterPreview;
    private Button btnSaveChanges;

    private DatabaseWorker dbWorker;
    private String eventID;
    private Event currentEvent; // To hold the data we fetch

    private android.net.Uri imageUri; // Stores the image path on the phone

    // Format used to display dates in the EditText
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
    private androidx.activity.result.ActivityResultLauncher<String> selectImageLauncher; //Opens gallery


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events); // Reusing the Create Layout

        selectImageLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        // Show the selected image on screen
                        ((android.widget.ImageView) findViewById(R.id.upload_poster_preview)).setImageURI(uri);
                        // Hide the upload icon so the image is visible
                        findViewById(R.id.iv_upload_icon).setVisibility(View.GONE);
                    }
                }
        );

        View imageBox = findViewById(R.id.fl_poster_upload);


        imageBox.setOnClickListener(v -> {
            selectImageLauncher.launch("image/*");
        });


        dbWorker = new DatabaseWorker();

        //et the Event ID passed from the previous activity
        eventID = getIntent().getStringExtra("EventID");

        if (eventID == null) {
            Toast.makeText(this, "Error: No Event ID found.", Toast.LENGTH_SHORT).show();
            finish(); // Close if no ID
            return;
        }


        initViews();
        btnSaveChanges.setText("Save Changes");
        loadEventData();


        btnSaveChanges.setOnClickListener(v -> {

            String newName = etEventName.getText().toString();
            String newLocation = etLocation.getText().toString();
            String newDesc = etDescription.getText().toString();
            String newTagsStr = etTags.getText().toString();
            String newCapacityStr = etCapacity.getText().toString();
            String newWaitlistStr = etWaitlistLimit.getText().toString();


            if (newName.isEmpty() || newLocation.isEmpty() || newDesc.isEmpty() ||
                    newCapacityStr.isEmpty() || newWaitlistStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            int newCapacity, newWaitlistLimit;
            try {
                newCapacity = Integer.parseInt(newCapacityStr);
                newWaitlistLimit = Integer.parseInt(newWaitlistStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number for capacity/waitlist", Toast.LENGTH_SHORT).show();
                return;
            }


            Date newStart, newEnd, enrollStart, enrollEnd;
            try {
                newStart = sdf.parse(etEventStart.getText().toString());
                newEnd = sdf.parse(etEventEnd.getText().toString());
                enrollStart = sdf.parse(etEnrollStart.getText().toString());
                enrollEnd = sdf.parse(etEnrollEnd.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
                return;
            }


            ArrayList<String> newTagsList = new ArrayList<>();
            if (!newTagsStr.isEmpty()) {
                String[] parts = newTagsStr.split("\\s+"); // Split by whitespace
                for (String p : parts) newTagsList.add(p);
            }


            String finalImageString;


            if (imageUri != null) {

                Toast.makeText(this, "Processing new image...", Toast.LENGTH_SHORT).show();
                finalImageString = encodeImage(imageUri);
                if (finalImageString == null) {
                    Toast.makeText(this, "New image is too large.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                finalImageString = currentEvent.getImage(); // Keep the old one
            }


            currentEvent.setName(newName);
            currentEvent.setLocation(newLocation);
            currentEvent.setDescription(newDesc);
            currentEvent.setCapacity(newCapacity);
            currentEvent.setStart_time(newStart);
            currentEvent.setEnd_time(newEnd);
            currentEvent.setEnrollement_start(enrollStart);
            currentEvent.setEnrollement_end(enrollEnd);
            currentEvent.setTags(newTagsList);
            currentEvent.setImage(finalImageString); // Update image



            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

            db.collection("Events").document(eventID)
                    .set(currentEvent) // Overwrite the document with new data
                    .addOnSuccessListener(aVoid -> {

                        // Also update the separate settings file for waitlist limit if you use it
                        new WaitlistManager().setWaitlistLimit(eventID, newWaitlistLimit);

                        Toast.makeText(this, "Changes Saved!", Toast.LENGTH_SHORT).show();

                        // Go back to the previous screen
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        Log.e("EditEvent", "Update failed", e);
                    });

        });

    }

    private void initViews() {
        etEventName = findViewById(R.id.et_event_name);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);
        etTags = findViewById(R.id.et_tag);
        etCapacity = findViewById(R.id.et_capacity);
        etWaitlistLimit = findViewById(R.id.et_waiting_list_limit);
        etEventStart = findViewById(R.id.et_event_start_time);
        etEventEnd = findViewById(R.id.et_event_end_time);
        etEnrollStart = findViewById(R.id.et_registration_period_start);
        etEnrollEnd = findViewById(R.id.et_registration_period_end);
        ivPosterPreview = findViewById(R.id.iv_poster_preview);
        btnSaveChanges = findViewById(R.id.submit_btn); // Reusing submit button

        // Hide the upload icon immediately since we are loading an existing image
        findViewById(R.id.iv_upload_icon).setVisibility(View.GONE);
    }

    private void loadEventData() {


        dbWorker.getEventByID(eventID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Convert DB document to Event Object
                currentEvent = dbWorker.convertDocumentToEvent(documentSnapshot);

                if (currentEvent != null) {
                    populateFields(currentEvent);
                }
            } else {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching event.", Toast.LENGTH_SHORT).show();
        });
    }


    private void populateFields(Event event) {

        etEventName.setText(event.getName());
        etLocation.setText(event.getLocation());
        etDescription.setText(event.getDescription());
        etCapacity.setText(String.valueOf(event.getCapacity()));


        if (event.getStart_time() != null) etEventStart.setText(sdf.format(event.getStart_time()));
        if (event.getEnd_time() != null) etEventEnd.setText(sdf.format(event.getEnd_time()));
        if (event.getEnrollement_start() != null) etEnrollStart.setText(sdf.format(event.getEnrollement_start()));
        if (event.getEnrollement_end() != null) etEnrollEnd.setText(sdf.format(event.getEnrollement_end()));


        ArrayList<String> tags = event.getTags();
        if (tags != null && !tags.isEmpty()) {
            // Join the list with spaces
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                etTags.setText(String.join(" ", tags));
            } else {
                // Manual join for older Android versions
                StringBuilder sb = new StringBuilder();
                for (String t : tags) sb.append(t).append(" ");
                etTags.setText(sb.toString().trim());
            }
        }




        String imageString = event.getImage();
        if (imageString != null && !imageString.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                Glide.with(this)
                        .load(imageBytes)
                        .fitCenter()
                        .into(ivPosterPreview);

            } catch (Exception e) {
                Log.e("EditEvent", "Failed to load image", e);
            }
        }
    }

    private String encodeImage(Uri imageUri) {
        try {
            //Get Bitmap
            android.graphics.Bitmap originalBitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            //Resize to max 800px width
            int maxWidth = 800;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            if (width > maxWidth) {
                float ratio = (float) width / maxWidth;
                width = maxWidth;
                height = (int) (height / ratio);
                originalBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, true);
            }

            //Compress
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            originalBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] bytes = stream.toByteArray();

            //Encode
            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}