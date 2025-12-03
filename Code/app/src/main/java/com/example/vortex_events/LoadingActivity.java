package com.example.vortex_events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingActivity extends AppCompatActivity {

    /**
     * Lightweight launcher activity that decides whether to show the main app or
     * the welcome/registration flow based on whether the device ID has an associated user.
     */

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DatabaseWorker dbWorker = new DatabaseWorker(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);


        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        dbWorker.checkIfIn(userID, new UserCheckCallBack() {
            @Override
            public void onUserChecked(boolean exists) {
                if (exists){
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if(!exists){
                    Intent intent = new Intent(LoadingActivity.this, WelcomePage.class);
                    startActivity(intent);
                }
            }

            @Override
            public void rUserCollected(RegisteredUser user) {

            }

            @Override
            public Void gUserCollected(GuestUser user) {
                return null;
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}