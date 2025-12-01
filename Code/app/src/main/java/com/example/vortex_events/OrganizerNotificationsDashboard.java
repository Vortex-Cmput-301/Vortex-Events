package com.example.vortex_events;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class OrganizerNotificationsDashboard extends AppCompatActivity {
    String eventID;
    String deviceID;

    Event currentEvent;
    AppNotification notification;
    String notifyMode;


    DatabaseWorker dbWorker;


    EditText notificationTitleEditor;
    EditText notificationContentEditor;

    Button pushToWaitlist;



    Button pushToAccepted;
    Button pushToDeclined;




    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_notifications_dashboard);

        Intent returnedID = getIntent();
        eventID = returnedID.getStringExtra("eventID");
        notifyMode = returnedID.getStringExtra("notifyMode");

        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        dbWorker = new DatabaseWorker();


        dbWorker.getEventByID(eventID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot docuemnt = task.getResult();
                    if (docuemnt.exists()){
                        currentEvent = DatabaseWorker.convertDocumentToEvent(docuemnt);
                    }else {
                        Log.d("EVENT CONVERSION", "EVENT DONT EXIST");
                    }
                }else {
                    Log.e(TAG, "ERROR GETTING EVENT");
                }
            }
        });

//        Get all UI elements
        notificationContentEditor = findViewById(R.id.notification_content_editor);
        notificationTitleEditor = findViewById(R.id.notification_title_editor);
        pushToWaitlist = findViewById(R.id.btn_push_to_declined);




        pushToWaitlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = notificationTitleEditor.getText().toString();
                String content = notificationContentEditor.getText().toString();
                String notification_id = UUID.randomUUID().toString();
                ArrayList<String> recievers = new ArrayList<>();



                ArrayList<String> group = new ArrayList<>();

                if (notifyMode.equals("Accepted")){
                    group = currentEvent.accepted;
                }else if (notifyMode.equals("Declined")){
                    group = currentEvent.declined;
                }else if (notifyMode.equals("Waitlist")){
                    group = currentEvent.waitlist;
                }

                for (String user: group){
                    dbWorker.getUserByDeviceID(user).addOnCompleteListener(new OnCompleteListener<RegisteredUser>() {
                        @Override
                        public void onComplete(@NonNull Task<RegisteredUser> task) {
                            if (task.isSuccessful()){
                                RegisteredUser user = task.getResult();
                                ArrayList<String> notificartions = user.notifications;
                                notificartions.add(notification_id);
                                dbWorker.pushNotiToUser(notificartions, user.deviceID);
                                recievers.add(user.notificationToken);
                                Log.d("NOTIFICATIONM SENDING", "WORKED");
                            }else {
                                Log.d("USER FETCHING", "USER DONT EXIST");
                            }
                        }
                    });
                }
                notification = new AppNotification(notification_id, deviceID, title, content, recievers);
                dbWorker.pushNotificationToDB(notification);
                Intent intent = new Intent(OrganizerNotificationsDashboard.this, OrganizerViewParticipant.class);
                intent.putExtra("EventID", eventID);
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