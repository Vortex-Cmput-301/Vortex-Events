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

    public RegisteredUser(Context context, String number, String email, String name){
        super(context);
        this.phone_number = number;
        this.email = email;
        this.name = name;

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
