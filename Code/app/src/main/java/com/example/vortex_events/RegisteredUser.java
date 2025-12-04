package com.example.vortex_events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a registered user in the system with profile information, event participation,
 * and notification preferences.
 */
public class RegisteredUser extends Users{
    String phone_number;
    String email;
    String name;
    boolean notifications_opted;
    String notificationToken;
    double latitude;
    double longitude;
    ArrayList<String> signed_up_events;
    Map<String, String> event_history;
    ArrayList<String> created_events;
    ArrayList<String> notifications;
    // Event status constants
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_NOT_CHOSEN = "NOT_CHOSEN";
    public static final String STATUS_REGISTERED = "REGISTERED";

    /**
     * Full constructor for RegisteredUser with all fields.
     * @param deviceID unique device identifier
     * @param phoneNumber user's phone number
     * @param email user's email address
     * @param name user's name
     * @param token FCM notification token
     * @param latitude user's latitude coordinate
     * @param longitude user's longitude coordinate
     * @param type user type (e.g., "Registered User", "Admin")
     * @param opted whether user opted into notifications
     */
    public RegisteredUser(String deviceID, String phoneNumber, String email, String name, String token, double latitude, double longitude, String type, boolean opted){
        this.deviceID = deviceID;
        this.phone_number = phoneNumber;
        this.email = email;
        this.name = name;
        this.notificationToken = token;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.notifications_opted = opted;
        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    /**
     * Default no-arg constructor required for Firestore deserialization.
     */
    public RegisteredUser(){
        // required by firebase
    }


    /** @return list of event IDs created by this user */
    /** @return list of event IDs created by this user */
    public ArrayList<String> getCreated_events() {
        return created_events;
    }

    /** @param created_events list of created event IDs to set */
    public void setCreated_events(ArrayList<String> created_events) {
        this.created_events = created_events;
    }

    /** @return user's email address */
    public String getEmail() {
        return email;
    }

    /** @param email email address to set */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return map of event IDs to their status in history */
    public Map<String, String> getEvent_history() {
        return event_history;
    }

    /** @param event_history event history map to set */
    public void setEvent_history(Map<String, String> event_history) {
        this.event_history = event_history;
    }

    /** @return user's latitude coordinate */
    public double getLatitude() {
        return latitude;
    }

    /** @param latitude latitude coordinate to set */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /** @return user's longitude coordinate */
    public double getLongitude() {
        return longitude;
    }

    /** @param longitude longitude coordinate to set */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /** @return user's name */
    public String getName() {
        return name;
    }

    /** @param name user's name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return list of notification IDs for this user */
    public ArrayList<String> getNotifications() {
        return notifications;
    }

    /** @param notifications notification list to set */
    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    /** @return FCM notification token */
    public String getNotificationToken() {
        return notificationToken;
    }

    /** @param notificationToken FCM token to set */
    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    /** @return user's phone number */
    public String getPhone_number() {
        return phone_number;
    }

    /** @param phone_number phone number to set */
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /** @return list of signed-up event IDs */
    public ArrayList<String> getSigned_up_events() {
        return signed_up_events;
    }

    /** @param signed_up_events list of signed-up event IDs to set */
    public void setSigned_up_events(ArrayList<String> signed_up_events) {
        this.signed_up_events = signed_up_events;
    }

    /** @return true if user opted into notifications */
    public boolean isNotifications_opted() {
        return notifications_opted;
    }

    /** @param notifications_opted notification opt-in status to set */
    public void setNotifications_opted(boolean notifications_opted) {
        this.notifications_opted = notifications_opted;
    }

    /**
     * Constructor with Context to auto-retrieve device ID.
     * @param context application or activity context
     * @param number phone number
     * @param email email address
     * @param name user's name
     * @param notificationToken FCM token
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     * @param opted notification opt-in status
     */
    public RegisteredUser(Context context, String number, String email, String name, String notificationToken, double latitude, double longitude, boolean opted){
        super(context);
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.notificationToken = notificationToken;

        this.latitude = latitude;
        this.longitude = longitude;

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
        this.notifications_opted = opted;
    }

    /**
     * Constructor with device ID (no context required).
     * @param deviceID unique device identifier
     * @param number phone number
     * @param email email address
     * @param name user's name
     * @param notificationToken FCM token
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     * @param opted notification opt-in status
     */
    public RegisteredUser(String deviceID, String number, String email, String name, String notificationToken, double latitude, double longitude, boolean opted, String type){
        this.deviceID = deviceID;
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.notificationToken = notificationToken;

        this.latitude = latitude;
        this.longitude = longitude;

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
        this.type = type;
        this.notifications_opted = opted;

    }

//    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude){
//        this(Id, number, email, name, latitude, longitude, "Registered User");
//    }
//    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude, String type){
//        super();
//        this.deviceID = Id;
//        this.phone_number = number;
//        this.email = email;
//        this.name = name;
//        this.type = type;  // add type
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.signed_up_events = new ArrayList<>();
//        this.created_events = new ArrayList<>();
//        this.event_history = new HashMap<>();
//        this.notifications = new ArrayList<>();
//    }


    /**
     * Move an event from signed-up list to history with a status.
     * @param eventID event ID to move
     * @param status status to record in history (e.g., STATUS_CANCELLED)
     * @return true if the event was in signed_up_events and was moved, false otherwise
     */
    public boolean moveToHistory(String eventID, String status) {
        if (signed_up_events.contains(eventID)) {
            signed_up_events.remove(eventID);
            event_history.put(eventID, status);
            return true;
        }
        return false;
    }

    /**
     * Callback interface for leave event operations.
     */
    public interface LeaveEventCallback { // *** NEW
        /** Called when leave operation succeeds */
        void onSuccess();
        /**
         * Called when leave operation fails.
         * @param e exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Remove this user from an event's waitlist and update both user and event in database.
     * @param targetEvent the event to leave
     * @param dbWorker database worker for persistence
     * @param callback callback for operation result
     */
    public void leaveEvent(Event targetEvent, DatabaseWorker dbWorker, LeaveEventCallback callback) { // *** CHANGED
        if (targetEvent == null || dbWorker == null) {
            if (callback != null) {
                callback.onFailure(new IllegalArgumentException("targetEvent or dbWorker is null"));
            }
            return;
        }

        // Ensure waitlist is not null
        if (targetEvent.getWaitlist() == null) {
            targetEvent.setWaitlist(new ArrayList<String>());
        }

        // Update in-memory user state
        boolean inSignedUp = signed_up_events != null && signed_up_events.contains(targetEvent.getEventID());
        if (inSignedUp) {
            moveToHistory(targetEvent.getEventID(), STATUS_CANCELLED);
        }

        // Remove from event waitlist in memory
        targetEvent.getWaitlist().remove(deviceID);

        // Persist user and event waitlist
        dbWorker.updateUser(this).addOnSuccessListener(aVoid -> {
            dbWorker.updateWaitlist(targetEvent.getWaitlist(), targetEvent.getEventID())
                    .addOnSuccessListener(aVoid2 -> {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    });
        }).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Get the status of a specific event from history.
     * @param eventID event ID to query
     * @return status string or null if not in history
     */
    public String getEventStatus(String eventID) {
        return event_history.get(eventID);
    }

    /**
     * Check if an event is in the user's history.
     * @param eventID event ID to check
     * @return true if event is in history
     */
    public boolean isEventInHistory(String eventID) {
        return event_history.containsKey(eventID);
    }

    /**
     * Get all event IDs from user's history.
     * @return list of event IDs in history
     */
    public ArrayList<String> getHistoricalEventIDs() {
        return new ArrayList<>(event_history.keySet());
    }

    /**
     * Get a copy of the event history map.
     * @return map of event IDs to statuses
     */
    public Map<String, String> getEventHistory() {
        return new HashMap<>(event_history);
    }

    /**
     * Add an event to the signed-up events list.
     * @param eventID event ID to add
     */
    public void addSignedUpEvent(String eventID) {
        if (signed_up_events == null) {
            signed_up_events = new ArrayList<>();
        }
        signed_up_events.add(eventID);
    }
}