package com.example.vortex_events;

import android.util.Log;

import java.util.ArrayList;

public class Entrant {
    String name;
    String email;

    ArrayList<Event> currentEvents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Event> getCurrentEvents() {
        return currentEvents;
    }

    public void setCurrentEvents(ArrayList<Event> currentEvents) {
        this.currentEvents = currentEvents;
    }

    public ArrayList<Event> getAllEvents() {
        return allEvents;
    }

    public void setAllEvents(ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
    }

    ArrayList<Event> allEvents;

    public Entrant(String name, String email) {
        this.name = name;


        this.email = email;
        this.allEvents = new ArrayList<>();
        this.currentEvents = new ArrayList<>();


    }


}
