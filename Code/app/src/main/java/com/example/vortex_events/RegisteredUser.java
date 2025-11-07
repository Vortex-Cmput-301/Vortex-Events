package com.example.vortex_events;

import android.content.Context;


import java.util.ArrayList;

public class RegisteredUser extends Users{
    String phone_number;
    String email;
    String name;
    ArrayList<String> signed_up_events;
    ArrayList<String> event_history;
    ArrayList<String> created_events;
    ArrayList<AppNotification> notifications;

    public ArrayList<Event> getCreated_events() {
        return created_events;
    }

    public void setCreated_events(ArrayList<Event> created_events) {
        this.created_events = created_events;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Event> getEvent_history() {
        return event_history;
    }

    public void setEvent_history(ArrayList<Event> event_history) {
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

    public ArrayList<Event> getSigned_up_events() {
        return signed_up_events;
    }

    public void setSigned_up_events(ArrayList<Event> signed_up_events) {
        this.signed_up_events = signed_up_events;
    }

    public RegisteredUser(Context context, String number, String email, String name){
        super(context);
        this.phone_number = number;
        this.email = email;
        this.name = name;
        this.type = "Registered User";

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history =  new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public RegisteredUser(String Id, String number, String email, String name){
        this.deviceID = Id;
        this.phone_number = number;
        this.email = email;
        this.name = name;

        this.signed_up_events = new ArrayList<>();
        this.created_events = new ArrayList<>();
        this.event_history =  new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public boolean leaveEvent(Event targetEvent){
        if (signed_up_events.contains(targetEvent.getEventID())){
            targetEvent.getWaitlist().remove(deviceID);//remove from waitlist
            signed_up_events.remove(targetEvent.getEventID());//remove from signed up events
            event_history.add(targetEvent.getEventID());//add to event history
            return true;

        }
        return false;
    }



}
