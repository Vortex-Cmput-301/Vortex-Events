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
    ArrayList<Users> waitlist;
    ArrayList<Users> accepted;
    ArrayList<Users> declined;
    String organizer;

    public Event(String name, String location, String organizer, String eventID, Date enrollement_start, Date enrollement_end, Date end_time, Date start_time, ArrayList<String> tags, String description, int capacity) {
        this.name = name;
        this.location = location;
        this.organizer = organizer;
        this.eventID = eventID;
        this.enrollement_start = enrollement_start;
        this.enrollement_end = enrollement_end;
        this.end_time = end_time;
        this.start_time = start_time;
        this.tags = tags;
        this.description = description;
        this.capacity = capacity;

        this.waitlist = new ArrayList<>();
        this.accepted = new ArrayList<>();
        this.declined = new ArrayList<>();
    }
}
