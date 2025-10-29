package com.example.vortex_events;

import java.util.ArrayList;

public class Event {
    String name;
    String description;
    ArrayList<Entrant> entrants;

    public Event(String name, String description) {
        this.name = name;
        this.description = description;
        this.entrants = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Entrant> getEntrants() {
        return entrants;
    }

    public void setEntrants(ArrayList<Entrant> entrants) {
        this.entrants = entrants;
    }
}
