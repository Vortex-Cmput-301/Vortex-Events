package com.example.vortex_events;

public class HashWorker {
    public HashWorker() {
    }


    /**
     * generateEventID() is a method for hashing the event name and organizer into an eventID by
     * reversing the name of the event and adding the first index of the deviceID to the string
     * @param eventName The name of the event
     * @param organizer The organizer's device ID
     * @return The eventID
     * */
    public String generateEventID(String eventName, String organizer){


        StringBuilder sb = new StringBuilder(eventName);
        String reversedName = sb.reverse().toString();
        String part1 = organizer.substring(0, 1);
        return  part1 + reversedName;
    }

    /**
     * eventIDtoName() is a method for translating the eventID back into an event name
     * @param eventID the eventID
     * @return the event name
     * **/
    public String eventIDToName(String eventID){
        String part1 = eventID.substring(0, 1);
        String reversed = eventID.substring(1, eventID.length());
        StringBuilder sb = new StringBuilder(reversed);

        return sb.reverse().toString();
    }
}

