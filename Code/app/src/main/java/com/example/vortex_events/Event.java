package com.example.vortex_events;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Event {
    String eventName;
    /*
    All variables below need to be update if related class is built.
    */
    String location;
    ArrayList<String> tagList = new ArrayList<>();
    int maximumCapacity;
    String eventTime; //YYYY-MM-DD, HH:MM-HH:MM. not the enroll period. Keep it a string please, use "eventTime = someClass.getString()" to update
    String enrollStart;//YYYY-MM-DD, (HH:MM) optional hours and minute. Keep it a string please, use "eventTime = someClass.getString()" to update
    String enrollEnd;  //YYYY-MM-DD, (HH:MM) optional hours and minute. Keep it a string please, use "eventTime = someClass.getString()" to update
    String Description;

    LinkedHashSet<String> waitingList = new LinkedHashSet<>();
    LinkedHashSet<String> chosenEntrants = new LinkedHashSet<>();// One idea is create a Entrants class with the state of chosen, lost, declined or cancelled




}
