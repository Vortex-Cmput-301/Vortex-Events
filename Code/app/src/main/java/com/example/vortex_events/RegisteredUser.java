package com.example.vortex_events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisteredUser extends Users{
    String phone_number;
    String email;
    String name;
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

    public RegisteredUser(){

    };

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<AppNotification> getNotifications() {
    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
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

    public RegisteredUser(Context context, String number, String email, String name, double latitude, double longitude){
    public RegisteredUser(Context context, String number, String email, String name, String notificationToken){
        super(context);
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notificationToken = notificationToken;

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude){
        this(Id, number, email, name, latitude, longitude, "Registered User");
    }
    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude, String type){
        super();
    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude){
        this(Id, number, email, name, latitude, longitude, "Registered User");
    }
    public RegisteredUser(String Id, String number, String email, String name, double latitude, double longitude, String type){
        super();
        this.deviceID = Id;
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.type = type;  // add type
        this.type = type;  // add type
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = "Registered User";
        this.latitude = latitude;
        this.longitude = longitude;
        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history = new HashMap<>();
        this.notifications = new ArrayList<>();
    }


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
            return moveToHistory(targetEvent.getEventID(), STATUS_CANCELLED);
        }
        return false;
    }

    public String getEventStatus(String eventID) {
        return event_history.get(eventID);
    }

    public boolean isEventInHistory(String eventID) {
        return event_history.containsKey(eventID);
    }

    public ArrayList<String> getHistoricalEventIDs() {
        return new ArrayList<>(event_history.keySet());
    }

    public Map<String, String> getEventHistory() {
        return new HashMap<>(event_history);
    }

    public void addSignedUpEvent(String eventID) {
        if (signed_up_events == null) {
            signed_up_events = new ArrayList<>();
        }
        signed_up_events.add(eventID);
    }
}
