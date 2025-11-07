package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomePage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseWorker dbWorker = new DatabaseWorker(db);

    Button signInButton;
    TextView guestLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);







        signInButton = findViewById(R.id.welcome_sign_in);
        guestLink = findViewById(R.id.guest_link);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomePage.this, RegistrationScreen.class);
                startActivity(intent);
            }
        });

        guestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuestUser guest = new GuestUser(WelcomePage.this);
                dbWorker.createGuest(guest);
                Intent intent = new Intent(WelcomePage.this, MainActivity.class);
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