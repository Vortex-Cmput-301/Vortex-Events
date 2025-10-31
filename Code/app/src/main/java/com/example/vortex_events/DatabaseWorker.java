package com.example.vortex_events;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;


    public DatabaseWorker(FirebaseFirestore db_arg) {
        this.db = db_arg;
        this.eventsRef = db.collection("Events");

        eventsRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
        }));
    }

    public Task<Void> createEvent(Users maker, Event targetEvent){
//        HashWorker hw = new HashWorker();
//        if (maker instanceof GuestUser){
//            return "Invalid permission";
//        }
//        targetEvent.setOrganizer(maker.deviceID);
//        targetEvent.setEventID(hw.generateEventID(targetEvent.getName(), maker.deviceID));
        DocumentReference docuref = eventsRef.document(targetEvent.getName());

        return docuref.set(targetEvent);
    }



}
