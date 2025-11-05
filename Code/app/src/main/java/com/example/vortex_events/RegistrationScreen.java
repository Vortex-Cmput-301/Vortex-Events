package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registration_activity);

        db = FirebaseFirestore.getInstance();
        dbWorker = new DatabaseWorker(db);

        phoneField = findViewById(R.id.phone_field);
        emailField = findViewById(R.id.email_field);
        nameField = findViewById(R.id.name_field_sign_up);






        signUpButton = findViewById(R.id.sign_up_submit);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = phoneField.getText().toString();
                emailAddress = emailField.getText().toString();
                userName = nameField.getText().toString();

                RegisteredUser user = new RegisteredUser(RegistrationScreen.this, phoneNumber, emailAddress, userName);
                dbWorker.createRegisteredUser(user);

                Intent intent = new Intent(RegistrationScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}