package com.example.vortex_events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Model class representing a registered user in the Vortex Events app.
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
     * Constructs a RegisteredUser with full details including type.
     *
     * @param deviceID the unique device ID of the user
     * @param phoneNumber the user's phone number
     * @param email the user's email address
     * @param name the user's name
     * @param token the FCM notification token
     * @param latitude last known latitude of the user
     * @param longitude last known longitude of the user
     * @param type the user type
     * @param opted true if the user opted into notifications
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
     * No argument constructor required by Firebase
     */
    public RegisteredUser(){
        // required by firebase
    }


    public ArrayList<String> getCreated_events() {
        return created_events;
    }

    public void setCreated_events(ArrayList<String> created_events) {
        this.created_events = created_events;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getEvent_history() {
        return event_history;
    }

    public void setEvent_history(Map<String, String> event_history) {
        this.event_history = event_history;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public ArrayList<String> getSigned_up_events() {
        return signed_up_events;
    }

    public void setSigned_up_events(ArrayList<String> signed_up_events) {
        this.signed_up_events = signed_up_events;
    }

    public boolean isNotifications_opted() {
        return notifications_opted;
    }

    public void setNotifications_opted(boolean notifications_opted) {
        this.notifications_opted = notifications_opted;
    }
    /**
     * Constructs a RegisteredUser using a Context to derive the device ID.
     *
     * @param context Android context used by the Users base class to get the device ID
     * @param number the user's phone number
     * @param email the user's email address
     * @param name the user's name
     * @param notificationToken the FCM notification token
     * @param latitude last known latitude
     * @param longitude last known longitude
     * @param opted true if the user has opted into notifications
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
     * Constructs a RegisteredUser using a provided device ID.
     *
     * @param deviceID the device ID for the user
     * @param number the user's phone number
     * @param email the user's email address
     * @param name the user's name
     * @param notificationToken the FCM notification token
     * @param latitude last known latitude
     * @param longitude last known longitude
     * @param opted true if the user has opted into notifications
     */
    public RegisteredUser(String deviceID, String number, String email, String name, String notificationToken, double latitude, double longitude, boolean opted){
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
        this.type = "Registered User";
        this.notifications_opted = opted;

    }
    /**
     * Moves an event from the signed-up list into the event history with a given status.
     *
     * @param eventID the ID of the event to move
     * @param status the status to record for the event (for example STATUS_CANCELLED)
     * @return true if the event was in the signed-up list and was moved, false otherwise
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
     * Callback interface for asynchronous event leave operations.
     */
    public interface LeaveEventCallback {
        /**
         * Called when leaveEvent successfully completes all updates.
         */
        void onSuccess();
        /**
         * Called when leaveEvent fails due to an error.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Removes this user from an event and updates the database.
     *
     * @param targetEvent the event the user is leaving
     * @param dbWorker the DatabaseWorker used to update Firestore
     * @param callback callback invoked on success or failure of the operation
     */
    public void leaveEvent(Event targetEvent, DatabaseWorker dbWorker, LeaveEventCallback callback) {
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
     * Returns the stored status for a given event ID from the event history.
     *
     * @param eventID the event ID to look up
     * @return the status string for that event, or null if not found
     */
    public String getEventStatus(String eventID) {
        return event_history.get(eventID);
    }

    /**
     * Checks whether an event is present in the user's event history.
     *
     * @param eventID the event ID to check
     * @return true if the event exists in event_history, false otherwise
     */
    public boolean isEventInHistory(String eventID) {
        return event_history.containsKey(eventID);
    }
    /**
     * Returns a list of all historical event IDs.
     *
     * @return a new ArrayList containing all keys from event_history
     */
    public ArrayList<String> getHistoricalEventIDs() {
        return new ArrayList<>(event_history.keySet());
    }
    /**
     * Returns a list of all historical event IDs.
     *
     * @return a new ArrayList containing all keys from event_history
     */
    public Map<String, String> getEventHistory() {
        return new HashMap<>(event_history);
    }
    /**
     * Adds an event to the user's signed-up event list.
     *
     * @param eventID the ID of the event to add
     */
    public void addSignedUpEvent(String eventID) {
        if (signed_up_events == null) {
            signed_up_events = new ArrayList<>();
        }
        signed_up_events.add(eventID);
    }
}