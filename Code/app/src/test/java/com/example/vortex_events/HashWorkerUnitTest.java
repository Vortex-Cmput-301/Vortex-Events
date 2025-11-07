package com.example.vortex_events;

import org.junit.Test;

import static org.junit.Assert.*;
public class HashWorkerUnitTest {


    @Test
    public void generateEventIDTest(){
        HashWorker hw = new HashWorker();
        String eventName = "Lebron party";
        String eventOrganizer = "Lebron";


        String EventID = hw.generateEventID(eventName, eventOrganizer);
        assertEquals("Lytrap norbeL", EventID);

        EventID = hw.generateEventID(eventOrganizer, eventName);
        assertEquals("LnorbeL", EventID);

        EventID = hw.generateEventID(eventOrganizer, eventOrganizer);
        assertEquals("LnorbeL", EventID);

        EventID = hw.generateEventID(eventName, eventName);
        assertEquals("Lytrap norbeL", EventID);


    }
}
