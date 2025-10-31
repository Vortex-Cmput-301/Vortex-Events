package com.example.vortex_events;

import android.content.Context;


import java.util.ArrayList;

public class RegisteredUser extends Users{
    String phone_number;
    String email;
    String name;
    ArrayList<Event> signed_up_events;
    ArrayList<Event> event_history;
    ArrayList<Event> created_events;
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


}
