package com.example.vortex_events;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DatabaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;
    CollectionReference usersRef;


    public DatabaseWorker(FirebaseFirestore db_arg) {
        this.db = db_arg;
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");
        eventsRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
        }));

        usersRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
        }));
    }


    public Task<Void> createGuest(GuestUser guest){
        DocumentReference docuRef = usersRef.document(guest.deviceID);

        return  docuRef.set(guest);

    }

    public boolean checkIfIn(String deviceID){
        final boolean[] exists = {false};
        DocumentReference docRef = db.collection("Users").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                exists[0] = task.isSuccessful();
            }
        });


        return exists[0];
    }

    public Task<Void> createRegisteredUser(RegisteredUser user){
        DocumentReference docuRef = usersRef.document(user.deviceID);

        return  docuRef.set(user);
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

    public Task<Void> updateEvent(Event targetEvent) {
        DocumentReference docuref = eventsRef.document(targetEvent.getName());

        return docuref.set(targetEvent);
    }

    public Task<Void> deleteEvent(Event targetEvent) {
        DocumentReference docuref = eventsRef.document(targetEvent.getName());

        return docuref.delete();
    }

    public Task<QuerySnapshot> getOrganizerEvents(String organizer) {
        return eventsRef.whereEqualTo("organizer", organizer).get();
    }
}
