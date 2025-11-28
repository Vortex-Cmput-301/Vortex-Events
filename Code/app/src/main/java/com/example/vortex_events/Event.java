package com.example.vortex_events;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    String eventID;
    String name;
    String description;
    String image;
    int capacity;
    int waitlist_limit;
    String location;
    ArrayList<String> tags;
    Date start_time;
    Date end_time;
    Date enrollement_start;
    Date enrollement_end;
    ArrayList<String> waitlist;
    ArrayList<String> accepted;
    ArrayList<String> declined;
    ArrayList<String> wonLottery;
    String organizer;
    boolean lottery_done;

    public Event() {

    }

    public Event(String name, String location, String organizer, String eventID, Date enrollement_start, Date enrollement_end, Date end_time, Date start_time, ArrayList<String> tags, String description, int capacity) {
        this.name = name;
        this.location = location;
        this.organizer = organizer;
        this.eventID = eventID;
        this.image = null; //TODO: add image
        this.enrollement_start = enrollement_start;
        this.enrollement_end = enrollement_end;
        this.end_time = end_time;
        this.start_time = start_time;
        this.tags = tags; // for filtering, put in an array of tags
        this.description = description;
        this.capacity = capacity;

        this.lottery_done = false;

        this.waitlist = new ArrayList<String>();
        this.accepted = new ArrayList<String>();
        this.declined = new ArrayList<String>();
    }

    public ArrayList<String> getWonLottery() {
        return wonLottery;
    }

    public void setWonLottery(ArrayList<String> wonLottery) {
        this.wonLottery = wonLottery;
    }

    public boolean isLottery_done() {
        return lottery_done;
    }

    public void setLottery_done(boolean lottery_done) {
        this.lottery_done = lottery_done;
    }



    public ArrayList<String> getAccepted() {
        return accepted;
    }

    public void setAccepted(ArrayList<String> accepted) {
        this.accepted = accepted;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<String> getDeclined() {
        return declined;
    }

    public void setDeclined(ArrayList<String> declined) {
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
    public String getImage() {

        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public ArrayList<String> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(ArrayList<String> waitlist) {
        this.waitlist = waitlist;
    }

    public int getWaitlist_limit() {
        return waitlist_limit;
    }
    public void setWaitlist_limit(int waitlist_limit) {
        this.waitlist_limit = waitlist_limit;
    }
}
