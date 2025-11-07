package com.example.vortex_events;

import android.content.Context;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisteredUser extends Users{
    String phone_number;
    String email;
    String name;
    ArrayList<String> signed_up_events;
    Map<String, String> event_history;
    ArrayList<String> created_events;
    ArrayList<AppNotification> notifications;
    // Event status constants
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_NOT_CHOSEN = "NOT_CHOSEN";

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

    public ArrayList<String> getEvent_history() {
        return event_history;
    }

    public void setEvent_history(ArrayList<String> event_history) {
        this.event_history = event_history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AppNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<AppNotification> notifications) {
        this.notifications = notifications;
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

    public RegisteredUser(Context context, String number, String email, String name){
        super(context);
        this.phone_number = number;
        this.email = email;
        this.name = name;

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    public RegisteredUser(String Id, String number, String email, String name){
        this.deviceID = Id;
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.type = "Registered User";

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
    }



    /**
     * Move an event to history with specified status
     * @param eventID The ID of the event to move to history
     * @param status The status of the event
     * @return true if successful, false if eventID not found in signed_up_events
     */
    public boolean moveToHistory(String eventID, String status) {
        if (signed_up_events.contains(eventID)) {
            signed_up_events.remove(eventID);
            event_history.put(eventID, status);
            return true;
        }
        return false;
    }

    public boolean leaveEvent(Event targetEvent){
        if (signed_up_events.contains(targetEvent.getEventID())) {
            if (targetEvent.getWaitlist() != null) {
                targetEvent.getWaitlist().remove(deviceID); // remove from waitlist
            }
            // Move to history with CANCELLED status
            return moveToHistory(targetEvent.getEventID(), STATUS_CANCELLED);
        }
        return false;
    }

    /**
     * Get the status of an event in history
     * @param eventID The ID of the event
     * @return The status of the event, or null if not found in history
     */
    public String getEventStatus(String eventID) {
        return event_history.get(eventID);
    }

    /**
     * Check if an event is in history
     * @param eventID The ID of the event
     * @return true if the event is in history, false otherwise
     */
    public boolean isEventInHistory(String eventID) {
        return event_history.containsKey(eventID);
    }

    /**
     * Get all event IDs in history
     * @return ArrayList of event IDs in history
     */
    public ArrayList<String> getHistoricalEventIDs() {
        return new ArrayList<>(event_history.keySet());
    }

    /**
     * Get the complete event history map
     * @return Map containing event IDs and their statuses
     */
    public Map<String, String> getEventHistory() {
        return new HashMap<>(event_history);
    }




}
