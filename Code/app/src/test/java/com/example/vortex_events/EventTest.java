package com.example.vortex_events;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * Simple tests for Event model.
 */
public class EventTest {

    @Test
    public void constructor_setsFieldsAndInitializesLists() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        Date now = new Date();
        Date later = new Date(now.getTime() + 3600_000L);

        Event event = new Event(
                "My Event",
                "Somewhere",
                "organizer123",
                "event123",
                now,
                later,
                later,
                now,
                tags,
                "description",
                50
        );

        assertEquals("My Event", event.getName());
        assertEquals("Somewhere", event.getLocation());
        assertEquals("organizer123", event.getOrganizer());
        assertEquals("event123", event.getEventID());
        assertEquals(50, event.getCapacity());
        assertEquals("description", event.getDescription());
        assertEquals(tags, event.getTags());
        assertFalse(event.isLottery_done());
        assertNotNull(event.getWaitlist());
        assertNotNull(event.getAccepted());
        assertNotNull(event.getDeclined());
    }

    @Test
    public void settersAndGetters_workForBasicFields() {
        Event event = new Event();
        event.setName("Test");
        event.setLocation("Location");
        event.setOrganizer("Org");
        event.setEventID("E1");
        event.setCapacity(10);

        assertEquals("Test", event.getName());
        assertEquals("Location", event.getLocation());
        assertEquals("Org", event.getOrganizer());
        assertEquals("E1", event.getEventID());
        assertEquals(10, event.getCapacity());
    }

    @Test
    public void waitlist_canBeModified() {
        Event event = new Event();
        ArrayList<String> waitlist = new ArrayList<>();
        event.setWaitlist(waitlist);

        waitlist.add("user1");
        waitlist.add("user2");

        assertEquals(2, event.getWaitlist().size());
        assertTrue(event.getWaitlist().contains("user1"));
    }
}
