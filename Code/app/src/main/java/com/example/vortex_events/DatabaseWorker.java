package com.example.vortex_events;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;
    CollectionReference usersRef; // 11.6 by Kehan - add users collection

    CollectionReference notificationsRef; // 11.22 by Saleh - For notifications collection



    /**
     * @Test for dependency injection
     * DO NOT USE FOR NON TESTING PURPOSES
     * */
    boolean userExists;


    public DatabaseWorker(FirebaseFirestore db_arg) {
        this.db = db_arg;
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");
        this.notificationsRef = db.collection("Notifications");

        eventsRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
        }));
    }

    public DatabaseWorker() {
        this.db = FirebaseFirestore.getInstance();
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");
        this.notificationsRef = db.collection("Notifications");

    }

    public boolean isUserExists() {
        return userExists;
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

    /**
     * For adding notifications to the database 11.22 - Saleh
     * **/
    public Task<Void> pushNotificationToDB(AppNotification notification){
        DocumentReference docuRef = notificationsRef.document(notification.notificationID);

        return docuRef.set(notification);
    }

    /**
     * For adding notifications to the users 11.22 - Saleh
     * **/
    public Task<Void> pushNotiToUser(List<String> newNotifications, String userID){
        return usersRef.document(userID).update("notifications", newNotifications);
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

    public Task<QuerySnapshot> getEventWaitlist(String eventID) {
        return eventsRef.document(eventID).collection("waitlist").get();
    }

    public Task<QuerySnapshot> getEventAccepted(String eventID) {
        return eventsRef.document(eventID).collection("accepted").get();
    }

    public Task<QuerySnapshot> getEventDeclined(String eventID) {
        return eventsRef.document(eventID).collection("declined").get();
    }

    public Task<Void> updateWaitlist(List<String> newList, String eventID){
       return eventsRef.document(eventID).update("waitlist", newList);
    }


    public Task<DocumentSnapshot> getEventByID(String id) {
        return eventsRef.document(id).get();
    }

    /**
     * get all events for Explore page
     * @return Task<QuerySnapshot>
     */
    //TODO: treat search by tag
    public Task<QuerySnapshot> getAllEvents() {
        Log.d("DatabaseWorker", "Getting all events");
        return eventsRef.get();
    }


    // 11.6 by Kehan - User related methods
    /**
     * creat user
     * @param user  new user object
     * @return Task<Void>
     */
    public Task<Void> createUser(RegisteredUser user) {
        Log.d("DatabaseWorker", "Creating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * User info update
     * @param user object need to update
     * @return Task<Void>
     */
    public Task<Void> updateUser(RegisteredUser user) {
        Log.d("DatabaseWorker", "Updating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * Delete user's profile
     * @param user object need to remove
     * @return Task<Void>
     */
    public Task<Void> deleteUser(RegisteredUser user) {
        Log.d("DatabaseWorker", "Deleting user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.delete();
    }

    /**
     * Get user by deviceID and convert to RegisteredUser object
     * @param deviceID
     * @return Task<RegisteredUser> stored user data as RegisteredUser object
     */
    public Task<RegisteredUser> getUserByDeviceID(String deviceID) {
        Log.d("DatabaseWorker", "Getting user by deviceID: " + deviceID);
        return usersRef.document(deviceID).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    return convertDocumentToRegisteredUser(document);
                } else {
                    Log.d("DatabaseWorker", "User not found with deviceID: " + deviceID);
                    return null;
                }
            } else {
                Log.e("DatabaseWorker", "Error getting user: ", task.getException());
                return null;
            }
        });
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


    /**
     * Convert DocumentSnapshot to RegisteredUser object
     * @param document DocumentSnapshot from Firestore
     * @return RegisteredUser object
     */
    private RegisteredUser convertDocumentToRegisteredUser(DocumentSnapshot document) {
        try {
            String deviceID = document.getId();
            String phoneNumber = document.getString("phone_number");
            String email = document.getString("email");
            String name = document.getString("name");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");


            // Handle lists - get them from document or create empty lists
            List<String> signedUpEvents = (List<String>) document.get("signed_up_events");
            Map<String, String> eventHistory = (Map<String, String>) document.get("event_history");
            List<String> createdEvents = (List<String>) document.get("created_events");
            List<AppNotification> notifications = (List<AppNotification>) document.get("notifications");

            // Create RegisteredUser object
            RegisteredUser user = new RegisteredUser(deviceID, phoneNumber, email, name, latitude, longitude);

            // Set the lists
            if (signedUpEvents != null) {
                user.signed_up_events = new ArrayList<>(signedUpEvents);
            }
            if (eventHistory != null) {
                user.event_history = new HashMap<>(eventHistory);
            }
            if (createdEvents != null) {
                user.created_events = new ArrayList<>(createdEvents);
            }
            if (notifications != null) {
                user.notifications = new ArrayList<>();
            }

            Log.d("DatabaseWorker", "Successfully converted document to RegisteredUser: " + deviceID);
            return user;

        } catch (Exception e) {
            Log.e("DatabaseWorker", "Error converting document to RegisteredUser: ", e);
            return null;
        }
    }

    /**
     * Convert DocumentSnapshot to Event object
     * @param document DocumentSnapshot from Firestore
     * @return Event object
     */
    public static Event convertDocumentToEvent(DocumentSnapshot document) {
        try {
            Event event = new Event();

            // Set basic properties
            event.setEventID(document.getId());
            event.setName(document.getString("name"));
            event.setDescription(document.getString("description"));
            event.setLocation(document.getString("location"));
            event.setOrganizer(document.getString("organizer"));
            event.setCapacity(document.getLong("capacity") != null ? document.getLong("capacity").intValue() : 0);

            // Set time properties
            event.setEnrollement_start(document.getDate("enrollement_start"));
            event.setEnrollement_end(document.getDate("enrollement_end"));
            event.setStart_time(document.getDate("start_time"));
            event.setEnd_time(document.getDate("end_time"));

            // Set tags list
            List<String> tags = (List<String>) document.get("tags");
            if (tags != null) {
                event.setTags(new ArrayList<>(tags));
            } else {
                event.setTags(new ArrayList<>());
            }

            // Set image URL
            event.setImage(document.getString("image"));

            // Load accepted participants (directly as ArrayList<String>)
            List<String> acceptedUserIDs = (List<String>) document.get("accepted");
            if (acceptedUserIDs != null) {
                event.setAccepted(new ArrayList<>(acceptedUserIDs));
            } else {
                event.setAccepted(new ArrayList<>());
            }

            // Load declined participants (directly as ArrayList<String>)
            List<String> declinedUserIDs = (List<String>) document.get("declined");
            if (declinedUserIDs != null) {
                event.setDeclined(new ArrayList<>(declinedUserIDs));
            } else {
                event.setDeclined(new ArrayList<>());
            }

            // Load waitlist participants (directly as ArrayList<String>)
            List<String> waitlistUserIDs = (List<String>) document.get("waitlist");
            if (waitlistUserIDs != null) {
                event.setWaitlist(new ArrayList<>(waitlistUserIDs));
            } else {
                event.setWaitlist(new ArrayList<>());
            }

            return event;

        } catch (Exception e) {
            Log.e("DatabaseWorker", "Error converting document to Event", e);
            return null;
        }
    }

}
