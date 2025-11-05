package com.example.vortex_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Add to every activity
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //Add the rest of the activities when finished
            //made a boolean function to implement highlighting items. will implement later
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home){
                    return true;
                }else if(itemId == R.id.nav_create) {
                    Intent intent = new Intent(getApplicationContext(), CreateActivityEvents.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });


    }


}