package com.example.vortex_events;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an event with metadata such as name, location, times,
 * capacity and participant lists (waitlist, accepted, declined, lottery winners).
 */
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

    /**
     * Default constructor required for deserialization and frameworks.
     */
    public Event() {

    }

    /**
     * Constructor for Event class
     * @param name Name of the event
     * @param location Location of the event
     * @param organizer Organizer of the event
     * @param eventID Unique ID of the event
     * @param enrollement_start Enrollment start date
     * @param enrollement_end Enrollment end date
     * @param end_time End time of the event
     * @param start_time Start time of the event
     * @param tags List of tags associated with the event
     * @param description Description of the event
     * @param capacity Capacity of the event
     */
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
        this.wonLottery = new ArrayList<String>();
    }

    
    /**
     * Returns the list of user IDs who won the lottery for this event.
     * @return list of winner user IDs
     */
    public ArrayList<String> getWonLottery() {
        return wonLottery;
    }

    /**
     * Sets the list of lottery winners for this event.
     * @param wonLottery list of winner user IDs
     */
    public void setWonLottery(ArrayList<String> wonLottery) {
        this.wonLottery = wonLottery;
    }

    /**
     * Indicates whether the lottery has been completed for this event.
     * @return true if lottery has been run, false otherwise
     */
    public boolean isLottery_done() {
        return lottery_done;
    }

    /**
     * Sets the lottery completion flag for this event.
     * @param lottery_done true if lottery has been run
     */
    public void setLottery_done(boolean lottery_done) {
        this.lottery_done = lottery_done;
    }



    /**
     * Returns the list of accepted participant user IDs.
     * @return list of accepted user IDs
     */
    public ArrayList<String> getAccepted() {
        return accepted;
    }

    /**
     * Sets the list of accepted participant user IDs.
     * @param accepted list of accepted user IDs
     */
    public void setAccepted(ArrayList<String> accepted) {
        this.accepted = accepted;
    }

    /**
     * Returns the capacity (maximum number of participants) for the event.
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the event capacity (maximum number of participants).
     * @param capacity maximum participants
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns the list of declined participant user IDs.
     * @return list of declined user IDs
     */
    public ArrayList<String> getDeclined() {
        return declined;
    }

    /**
     * Sets the list of declined participant user IDs.
     * @param declined list of declined user IDs
     */
    public void setDeclined(ArrayList<String> declined) {
        this.declined = declined;
    }

    /**
     * Returns the event description.
     * @return description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event description.
     * @param description description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the event end time.
     * @return end time
     */
    public Date getEnd_time() {
        return end_time;
    }

    /**
     * Sets the event end time.
     * @param end_time end time
     */
    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    /**
     * Returns the enrollment end date/time.
     * @return enrollment end
     */
    public Date getEnrollement_end() {
        return enrollement_end;
    }

    /**
     * Sets the enrollment end date/time.
     * @param enrollement_end enrollment end
     */
    public void setEnrollement_end(Date enrollement_end) {
        this.enrollement_end = enrollement_end;
    }

    /**
     * Returns the enrollment start date/time.
     * @return enrollment start
     */
    public Date getEnrollement_start() {
        return enrollement_start;
    }

    /**
     * Sets the enrollment start date/time.
     * @param enrollement_start enrollment start
     */
    public void setEnrollement_start(Date enrollement_start) {
        this.enrollement_start = enrollement_start;
    }
    /**
     * Returns the image URL or path for the event.
     * @return image string or null
     */
    public String getImage() {

        return image;
    }

    /**
     * Sets the image URL or path for the event.
     * @param image image string
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Returns the unique event ID.
     * @return event ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the unique event ID.
     * @param eventID event ID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Returns the event location.
     * @return location string
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the event location.
     * @param location location string
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the event name.
     * @return name string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the event name.
     * @param name name string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the organizer of the event.
     * @return organizer string
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Sets the event organizer.
     * @param organizer organizer string
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * Returns the event start time.
     * @return start time
     */
    public Date getStart_time() {
        return start_time;
    }

    /**
     * Sets the event start time.
     * @param start_time start time
     */
    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    /**
     * Returns the list of tags associated with the event.
     * @return list of tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags associated with the event.
     * @param tags list of tags
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * Returns the waitlist of user IDs for this event.
     * @return waitlist user IDs
     */
    public ArrayList<String> getWaitlist() {
        return waitlist;
    }

    /**
     * Sets the waitlist user IDs for this event.
     * @param waitlist list of user IDs
     */
    public void setWaitlist(ArrayList<String> waitlist) {
        this.waitlist = waitlist;
    }


}
