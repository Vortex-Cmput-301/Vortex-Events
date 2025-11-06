package com.example.vortex_events;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Account_settings extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone;
    private Button buttonSaveChanges;
    private ImageButton buttonBack;

    // Firebase Components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUser;
    private DocumentReference userDocRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //initialize variables needed
        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPhone = findViewById(R.id.edit_text_phone);
        buttonSaveChanges = findViewById(R.id.button_save_changes);
        buttonBack = findViewById(R.id.button_back);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        currentUser = mAuth.getCurrentUser();
        currentUser = "2c5e1fb22ef59572";
        // If the user is not null, get the reference to their document in Firestore
        if (currentUser != null) {
            userDocRef = db.collection("Users").document(currentUser);
        }

        if (currentUser == null) {
            // No user is signed in, so we can't show or save any data.
            // You should ideally redirect to the login screen.
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "OnCreate" + currentUser);
            finish(); // Close this activity
            return; // Stop further execution
        }
        setupClickListeners();
        loadUserData();

    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> finish()); // Simply close the activity

        buttonSaveChanges.setOnClickListener(v -> {
            // When the save button is clicked, call the method to update data
            saveUserData();
        });
    }

    /**
     * Fetches the current user's data from Firestore and populates the EditText fields.
     */
    private void loadUserData() {
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "User data found: " + document.getData());
                    // Get data from the document and set it in the EditText fields
                    // Use ".getString()" to safely get the data
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone_number"); // Make sure this field name matches your Firestore document

                    editTextName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);

                } else {
                    Log.d(TAG, "No such document for user: " + currentUser);
                    // This case happens if the user exists in Auth but not in Firestore.
                    // You might want to pre-fill with what you know, like the email.
                    editTextEmail.setText(currentUser);
                    Toast.makeText(this, "User profile not found, please create one.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Saves the data from the EditText fields back to the user's document in Firestore.
     */
    private void saveUserData() {
        // Get the new values from the EditText fields
        String newName = editTextName.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();
        String newPhone = editTextPhone.getText().toString().trim();

        // Basic validation: ensure the name is not empty
        if (newName.isEmpty()) {
            editTextName.setError("Name cannot be empty");
            editTextName.requestFocus();
            return;
        }

        // Show feedback to the user that saving is in progress
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

        // Create a Map to hold the data you want to update
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", newName);
        userData.put("email", newEmail);
        userData.put("phone_number", newPhone); // Ensure this key matches your Firestore field name

        // Update the document in Firestore
        userDocRef.update(userData)
                .addOnSuccessListener(aVoid -> {
                    // This block runs if the update is successful
                    Log.d(TAG, "User data successfully updated!");
                    Toast.makeText(Account_settings.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally, close the activity after saving
                })
                .addOnFailureListener(e -> {
                    // This block runs if the update fails
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(Account_settings.this, "Error saving changes. Please try again.", Toast.LENGTH_LONG).show();
                });
    }

}