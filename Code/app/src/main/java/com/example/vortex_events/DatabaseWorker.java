package com.example.vortex_events;
import android.util.Log;

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
    CollectionReference usersRef; // 11.6 by Kehan - add users collection


    /**
     * @Test for dependency injection
     * DO NOT USE FOR NON TESTING PURPOSES
     * */
    public DatabaseWorker(FirebaseFirestore db_arg) {
        this.db = db_arg;
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");// 11.6 by Kehan - add users collection

    }
    
    public DatabaseWorker() {
        this.db = FirebaseFirestore.getInstance();
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");// 11.6 by Kehan - add users collection
    }

    public Task<Void> createEvent(Users maker, Event targetEvent){
        HashWorker hw = new HashWorker();
        targetEvent.setOrganizer(maker.deviceID);
        targetEvent.setEventID(hw.generateEventID(targetEvent.getName(), maker.deviceID));
        DocumentReference docuref = eventsRef.document(targetEvent.getEventID());

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

    public Task<DocumentSnapshot> getEventByID(String id) {
        return eventsRef.document(id).get();
    }

    public Task<QuerySnapshot> getAllEvents() {
        return eventsRef.get();
    }


    // 11.6 by Kehan - User related methods
    /**
     * creat user
     * @param user  new user object
     * @return Task<Void>
     */
    public Task<Void> createUser(Users user) {
        Log.d("DatabaseWorker", "Creating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * User info update
     * @param user object need to update
     * @return Task<Void>
     */
    public Task<Void> updateUser(Users user) {
        Log.d("DatabaseWorker", "Updating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * Delete user's profile
     * @param user object need to remove
     * @return Task<Void>
     */
    public Task<Void> deleteUser(Users user) {
        Log.d("DatabaseWorker", "Deleting user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.delete();
    }

    /**
     * get user by deviceID
     * @param deviceID
     * @return Task<DocumentSnapshot> stored user data
     */
    public Task<DocumentSnapshot> getUserByDeviceID(String deviceID) {
        Log.d("DatabaseWorker", "Getting user by deviceID: " + deviceID);
        return usersRef.document(deviceID).get();
    }

    /**
     * check if user exists
     * @param deviceID
     * @return Task<Boolean> if user already in database or not
     */
    public Task<Boolean> userExists(String deviceID) {
        return usersRef.document(deviceID).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                return document != null && document.exists();
            }
            return false;
        });
    }

    /**
     * get all users only for testing, should not use in final app
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getAllUsers() {
        Log.d("DatabaseWorker", "Getting all users");
        return usersRef.get();
    }
}
