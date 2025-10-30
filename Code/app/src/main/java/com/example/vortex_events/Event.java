package com.example.vortex_events;

import com.google.firebase.firestore.auth.User;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    String eventID;
    String name;
    String description;

//    Image here somehow
    int capacity;
    String location;
    ArrayList<String> tags;
    Date start_time;
    Date end_time;
    Date enrollement_start;
    Date enrollement_end;
    ArrayList<User> waitlist;
    ArrayList<Users> accepted;
    ArrayList<User> declined;
    String organizer;

}
