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

    boolean userExists;


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

    public boolean isUserExists() {
        return userExists;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }

    public Task<Void> createGuest(GuestUser guest){
        DocumentReference docuRef = usersRef.document(guest.deviceID);

        return  docuRef.set(guest);

    }

    public void checkIfIn(String deviceID, final UserCheckCallBack callBack){


        DocumentReference docRef = db.collection("Users").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            boolean real = true;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("RETRIEVAL", "DocumentSnapshot data: " + document.getData());
                        callBack.onUserChecked(true);

                    } else {
                        Log.d("RETRIEVAL", "No such document");
                        callBack.onUserChecked(false);
                    }
                } else {
                    Log.d("RETRIEVAL", "get failed with ", task.getException());
                }
            }

        });
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
