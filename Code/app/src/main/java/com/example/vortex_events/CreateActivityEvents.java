package com.example.vortex_events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateActivityEvents extends AppCompatActivity {


    private void showDateTimePickerDialog(EditText fieldToUpdate) {
        // Get Current Date
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
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                                    // Store the selected date/time in a Calendar object
                                    Calendar selectedDateTime = Calendar.getInstance();
                                    selectedDateTime.set(year1, monthOfYear, dayOfMonth, hourOfDay, minute);

                                    // Format it to a string
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
                                    String formattedDate = sdf.format(selectedDateTime.getTime());

                                    // Set the string in the EditText field
                                    fieldToUpdate.setText(formattedDate);

                                }
                            },
                            hour, minute, true); // true for 24-hour time
                    timePickerDialog.show();
                },
                year, month, day);
        datePickerDialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View.OnClickListener dateTimePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog((EditText) v);
            }
        };

        EditText eventStartTimeField = findViewById(R.id.et_event_start_time);
        EditText eventEndTimeField = findViewById(R.id.et_event_end_time);
        EditText enrollStartTimeField = findViewById(R.id.et_registration_period_start);
        EditText enrollEndTimeField = findViewById(R.id.et_registration_period_end);

        eventStartTimeField.setOnClickListener(dateTimePickerListener);
        eventEndTimeField.setOnClickListener(dateTimePickerListener);
        enrollStartTimeField.setOnClickListener(dateTimePickerListener);
        enrollEndTimeField.setOnClickListener(dateTimePickerListener);

        Button submitButton = findViewById(R.id.submit_btn);

        //on click listener for the boxes
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventName = ((EditText) findViewById(R.id.et_event_name)).getText().toString();
                String eventLocation = ((EditText) findViewById(R.id.et_location)).getText().toString();
                String description = ((EditText) findViewById(R.id.et_description)).getText().toString();
                String tagString = ((EditText) findViewById(R.id.et_tag)).getText().toString();
                String capacityString = ((EditText) findViewById(R.id.et_capacity)).getText().toString();
                String waitingListString = ((EditText) findViewById(R.id.et_waiting_list_limit)).getText().toString();
                String eventStartString = ((EditText) findViewById(R.id.et_event_start_time)).getText().toString();
                String eventEndString = ((EditText) findViewById(R.id.et_event_end_time)).getText().toString();
                String enrollStartString = ((EditText) findViewById(R.id.et_registration_period_start)).getText().toString();
                String enrollEndString = ((EditText) findViewById(R.id.et_registration_period_end)).getText().toString();

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
                    return;
                }


                int capacity;

                int waitingListLimit;

                try {
                    capacity = Integer.parseInt(capacityString);
                    waitingListLimit = Integer.parseInt(waitingListString);
                } catch (NumberFormatException e) {
                    Log.e("FormData", "Failed to parse a number", e);
                    Toast.makeText(CreateActivityEvents.this, "Capacity must be a valid number", Toast.LENGTH_SHORT).show();
                    return;
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
                    enrollmentStartTime = sdf.parse(enrollStartString);
                    enrollmentEndTime = sdf.parse(enrollEndString);

                } catch (ParseException e) {
                    Log.e("FormData", "An impossible error occurred while parsing dates!");
                    Toast.makeText(CreateActivityEvents.this, "A critical error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagString.split(" ")));

                Log.d("FormData", "SUCCESS: All data validated and parsed.");
                Log.d("FormData", "Event Name: " + eventName);
                Log.d("FormData", "Location: " + eventLocation);
                Log.d("FormData", "Capacity: " + capacity);
                Log.d("FormData", "Event Start: " + eventStartTime);


                Event event = new Event(eventName, eventLocation, "Organizer", "12345", enrollmentStartTime,enrollmentEndTime, eventStartTime, eventEndTime, tagsList,description,capacity);



                Toast.makeText(CreateActivityEvents.this, "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });







        //Add to every activity-------
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
                }

                return false;
            }
        });


    }






}