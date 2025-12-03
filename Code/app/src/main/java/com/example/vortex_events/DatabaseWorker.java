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

/**
 * Central database worker for Firestore operations on Events, Users, and Notifications.
 * Provides CRUD operations and conversion utilities for documents.
 */
public class DatabaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;
    CollectionReference usersRef; // 11.6 by Kehan - add users collection

    CollectionReference notificationsRef; // 11.22 by Saleh - For notifications collection




    boolean userExists;

    /**
     * Dependency-injected constructor used for testing.
     * @param db_arg Firestore instance to use
     */
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

    /**
     * Default constructor - initializes with default Firestore instance.
     */
    public DatabaseWorker() {
        this.db = FirebaseFirestore.getInstance();
        this.eventsRef = db.collection("Events");
        this.usersRef = db.collection("Users");
        this.notificationsRef = db.collection("Notifications");

    }

    /**
     * Check if user exists flag.
     * @return true if user exists, false otherwise
     */
    public boolean isUserExists() {
        return userExists;
    }

    /**
     * Create a guest user in the database.
     * @param guest guest user object to create
     * @return task representing the operation
     */
    public Task<Void> createGuest(GuestUser guest){
        DocumentReference docuRef = usersRef.document(guest.deviceID);

        return  docuRef.set(guest);

    }

    /**
     * Check if a user exists in the database by device ID and invoke callback with result.
     * @param deviceID unique device identifier
     * @param callBack callback to receive existence check result
     */
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

    /**
     * Create a registered user in the database.
     * @param user registered user object to create
     * @return task representing the operation
     */
    public Task<Void> createRegisteredUser(RegisteredUser user){
        DocumentReference docuRef = usersRef.document(user.deviceID);

        return  docuRef.set(user);
    }

    /**
     * Push a notification object to the Notifications collection.
     * @param notification notification object to save
     * @return task representing the operation
     */
    public Task<Void> pushNotificationToDB(AppNotification notification){
        DocumentReference docuRef = notificationsRef.document(notification.notificationID);

        return docuRef.set(notification);
    }

    /**
     * Get all documents from the Notifications collection.
     * @return task resolving to a QuerySnapshot of all notifications
     */
    public Task<QuerySnapshot> getAllNotifications() {
        return notificationsRef.get();
    }

    /**
     * Delete a notification document by its ID from the Notifications collection.
     * @param notificationID document ID of the notification to delete
     * @return task representing the delete operation
     */
    public Task<Void> deleteNotificationById(String notificationID) {
        return notificationsRef.document(notificationID).delete();
    }

    /**
     * Update the notifications list for a specific user.
     * @param newNotifications the list of notification IDs to set
     * @param userID the user ID to update
     * @return task representing the update operation
     */
    public Task<Void> pushNotiToUser(List<String> newNotifications, String userID){
        return usersRef.document(userID).update("notifications", newNotifications);
    }


    /**
     * Create an event in the database.
     * @param maker the organizer (user) creating the event
     * @param targetEvent the event object to create
     * @return task representing the operation
     */
    public Task<Void> createEvent(Users maker, Event targetEvent){
        HashWorker hw = new HashWorker();
        targetEvent.setOrganizer(maker.deviceID);
        targetEvent.setEventID(hw.generateEventID(targetEvent.getName(), maker.deviceID));
        DocumentReference docuref = eventsRef.document(targetEvent.getEventID());

        return docuref.set(targetEvent);
    }

    /**
     * Update an existing event in the database.
     * @param targetEvent the event object with updated data
     * @return task representing the operation
     */
    public Task<Void> updateEvent(Event targetEvent) {
        DocumentReference docuref = eventsRef.document(targetEvent.getEventID());

        return docuref.set(targetEvent);
    }

    /**
     * Delete an event and clean up references in users' signed_up_events.
     * @param targetEvent the event to delete
     * @return task representing the operation
     */
    public Task<Void> deleteEvent(Event targetEvent) { // modified
        if (targetEvent == null || targetEvent.getEventID() == null || targetEvent.getEventID().isEmpty()) { 
            Log.e("DatabaseWorker", "deleteEvent: event or eventID is null/empty"); 
            return null; 
        } 

        String targetEventId = targetEvent.getEventID(); 

        // Step 1: query all users whose signed_up_events contains this eventID 
        return usersRef.whereArrayContains("signed_up_events", targetEventId) 
                .get() 
                .continueWithTask(task -> { 
                    if (!task.isSuccessful()) { 
                        Exception e = task.getException(); 
                        Log.e("DatabaseWorker", "Failed to query users for event cleanup", e); 
                        throw e != null ? e : new Exception("Unknown error querying users for event cleanup"); 
                    } 

                    QuerySnapshot querySnapshot = task.getResult(); 
                    if (querySnapshot != null) { 
                        for (DocumentSnapshot userDoc : querySnapshot.getDocuments()) { 
                            List<String> signedUpEvents = (List<String>) userDoc.get("signed_up_events"); 
                            if (signedUpEvents != null && signedUpEvents.contains(targetEventId)) { 
                                signedUpEvents.remove(targetEventId); 
                                usersRef.document(userDoc.getId()) 
                                        .update("signed_up_events", signedUpEvents) 
                                        .addOnFailureListener(e -> Log.e("DatabaseWorker", "Failed to update signed_up_events for user: " + userDoc.getId(), e)); 
                            } 
                        } 
                    } 

                    // Step 2: delete the event document itself 
                    DocumentReference docRef = eventsRef.document(targetEventId); 
                    return docRef.delete(); 
                }); 
    }


    /**
     * Get all events organized by a specific organizer.
     * @param organizer organizer device ID
     * @return task resolving to query snapshot of events
     */
    public Task<QuerySnapshot> getOrganizerEvents(String organizer) {
        return eventsRef.whereEqualTo("organizer", organizer).get();
    }

    /**
     * Get the waitlist subcollection for an event (deprecated pattern).
     * @param eventID event ID
     * @return task resolving to query snapshot
     */
    public Task<QuerySnapshot> getEventWaitlist(String eventID) {
        return eventsRef.document(eventID).collection("waitlist").get();
    }
    
    /**
     * Get accepted users subcollection for an event (deprecated pattern).
     * @param eventID event ID
     * @return task resolving to query snapshot
     */
    public Task<QuerySnapshot> getEventAccepted(String eventID) {
        return eventsRef.document(eventID).collection("accepted").get();
    }

    /**
     * Get declined users subcollection for an event (deprecated pattern).
     * @param eventID event ID
     * @return task resolving to query snapshot
     */
    public Task<QuerySnapshot> getEventDeclined(String eventID) {
        return eventsRef.document(eventID).collection("declined").get();
    }

    /**
     * Update the waitlist array field for an event.
     * @param newList updated list of user IDs
     * @param eventID event ID
     * @return task representing the operation
     */
    public Task<Void> updateWaitlist(List<String> newList, String eventID){
       return eventsRef.document(eventID).update("waitlist", newList);
    }

    /**
     * Get an event document by ID.
     * @param id event ID
     * @return task resolving to document snapshot
     */
    public Task<DocumentSnapshot> getEventByID(String id) {
        return eventsRef.document(id).get();
    }

    /**
     * Get all events (for Explore page).
     * @return task resolving to query snapshot of all events
     */
    public Task<QuerySnapshot> getAllEvents() {
        Log.d("DatabaseWorker", "Getting all events");
        return eventsRef.get();
    }


    /**
     * Create a new user in the database.
     * @param user new RegisteredUser object
     * @return task representing the operation
     */
    public Task<Void> createUser(RegisteredUser user) {
        Log.d("DatabaseWorker", "Creating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * Update user information in the database.
     * @param user RegisteredUser object with updated data
     * @return task representing the operation
     */
    public Task<Void> updateUser(RegisteredUser user) {
        Log.d("DatabaseWorker", "Updating user with deviceID: " + user.deviceID);
        DocumentReference docRef = usersRef.document(user.deviceID);
        return docRef.set(user);
    }

    /**
     * Delete a user's profile by device ID.
     * @param user RegisteredUser object to delete
     * @return task representing the operation
     */
    public Task<Void> deleteUser(RegisteredUser user) {
        if (user == null || user.getDeviceID() == null) {
            Log.e("DatabaseWorker", "deleteUser: user or deviceID is null");
            return null;
        }

        String deviceID = user.getDeviceID();
        Log.d("DatabaseWorker", "Deleting user with deviceID: " + deviceID);
        DocumentReference docRef = usersRef.document(deviceID);
        return docRef.delete();
    }

    /**
     * Get a user by device ID and convert to RegisteredUser object.
     * @param deviceID unique device identifier
     * @return task resolving to RegisteredUser object or null if not found
     */
    public Task<RegisteredUser> getUserByDeviceID(String deviceID) {
        Log.d("DatabaseWorker", "Getting user by deviceID: " + deviceID);
        return usersRef.document(deviceID).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d("DatabaseWorker", "Returning user document");

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
     * Check if a user exists by device ID.
     * @param deviceID unique device identifier
     * @return task resolving to true if user exists, false otherwise
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
     * Get all users (for admin/testing purposes only).
     * @return task resolving to query snapshot of all users
     */
    public Task<QuerySnapshot> getAllUsers() {
        Log.d("DatabaseWorker", "Getting all users");
        return usersRef.get();
    }


    /**
     * Convert a Firestore DocumentSnapshot to a RegisteredUser object.
     * @param document Firestore document snapshot
     * @return RegisteredUser object or null if conversion fails
     */
    private RegisteredUser convertDocumentToRegisteredUser(DocumentSnapshot document) {
        try {
            String deviceID = document.getString("deviceID");
            String phoneNumber = document.getString("phone_number");
            String email = document.getString("email");
            String name = document.getString("name");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");
            String type = document.getString("type");
            String token = document.getString("notificationToken");
            Boolean opted = document.getBoolean("notifications_opted");


            // Handle lists - get them from document or create empty lists
            List<String> signedUpEvents = (List<String>) document.get("signed_up_events");
            Map<String, String> eventHistory = (Map<String, String>) document.get("event_history");
            List<String> createdEvents = (List<String>) document.get("created_events");
            List<AppNotification> notifications = (List<AppNotification>) document.get("notifications");

            // Create RegisteredUser object
            RegisteredUser user = new RegisteredUser(deviceID, phoneNumber, email, name, token, latitude, longitude, opted);

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
     * Convert a Firestore DocumentSnapshot to an Event object.
     * @param document Firestore document snapshot
     * @return Event object or null if conversion fails
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

            List<String> wonUserIDs = (List<String>) document.get("wonLottery");
            if (waitlistUserIDs != null) {
                event.setWonLottery(new ArrayList<>(wonUserIDs));
            } else {
                event.setWonLottery(new ArrayList<>());
            }

            return event;

        } catch (Exception e) {
            Log.e("DatabaseWorker", "Error converting document to Event", e);
            return null;
        }
    }

    /**
     * Clear the image field of an event (for admin use).
     * @param eventID event ID to update
     * @return task representing the operation
     */
    public Task<Void> clearEventImage(String eventID) {
        return eventsRef.document(eventID).update("image", null);
    }

    /**
     * Replace the notifications array for a specific user (for admin use).
     * @param deviceID user device ID
     * @param notifications new list of notifications
     * @return task representing the operation
     */
    public Task<Void> updateUserNotifications(String deviceID, List<AppNotification> notifications) {
        return usersRef.document(deviceID).update("notifications", notifications);
    }


}
