package com.example.vortex_events;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class databaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;


    public databaseWorker() {
        this.db = FirebaseFirestore.getInstance();
        this.eventsRef = db.collection("Events");

        eventsRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
        }));
    }

    public String createEvent(Users maker, Event targetEvent){
//        HashWorker hw = new HashWorker();
//        if (maker instanceof GuestUser){
//            return "Invalid permission";
//        }
//        targetEvent.setOrganizer(maker.deviceID);
//        targetEvent.setEventID(hw.generateEventID(targetEvent.getName(), maker.deviceID));
        DocumentReference docuref = eventsRef.document(targetEvent.getName());
        docuref.set(targetEvent);
        return "Post valid";
    }



}
