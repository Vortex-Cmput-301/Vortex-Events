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



    public ArrayList<Users> getAccepted() {
        return accepted;
    }

    public void setAccepted(ArrayList<Users> accepted) {
        this.accepted = accepted;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Users> getDeclined() {
        return declined;
    }

    public void setDeclined(ArrayList<Users> declined) {
        this.declined = declined;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Date getEnrollement_end() {
        return enrollement_end;
    }

    public void setEnrollement_end(Date enrollement_end) {
        this.enrollement_end = enrollement_end;
    }

    public Date getEnrollement_start() {
        return enrollement_start;
    }

    public void setEnrollement_start(Date enrollement_start) {
        this.enrollement_start = enrollement_start;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<Users> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(ArrayList<Users> waitlist) {
        this.waitlist = waitlist;
    }
}
