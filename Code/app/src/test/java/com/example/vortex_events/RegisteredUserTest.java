package com.example.vortex_events;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for RegisteredUser.
 * These tests avoid using Android-specific constructors so they can run as plain JVM tests.
 */
public class RegisteredUserTest {

    private RegisteredUser createUserForLogicTest() {
        // Use the constructor that does not touch Android APIs
        return new RegisteredUser(
                "device123",
                "1234567890",
                "test@example.com",
                "Test User",
                53.0,
                -113.0,
                "Registered User"
        );
    }

    @Test
    public void constructor_initializesCollections() {
        RegisteredUser user = createUserForLogicTest();

        assertNotNull("signed_up_events should be initialized", user.getSigned_up_events());
        assertNotNull("created_events should be initialized", user.getCreated_events());
        assertNotNull("event_history should be initialized", user.getEvent_history());
        assertNotNull("notifications should be initialized", user.getNotifications());

        assertTrue(user.getSigned_up_events().isEmpty());
        assertTrue(user.getCreated_events().isEmpty());
        assertTrue(user.getEvent_history().isEmpty());
        assertTrue(user.getNotifications().isEmpty());
    }

    @Test
    public void addSignedUpEvent_addsEventId() {
        // Use a user that initially has null signed_up_events to test the null guard
        RegisteredUser user = new RegisteredUser(
                "deviceX",
                "000",
                "x@example.com",
                "X",
                0.0,
                0.0,
                "Registered User"
        );
        // force signed_up_events to null to simulate partially loaded object
        user.setSigned_up_events(null);

        user.addSignedUpEvent("event1");
        user.addSignedUpEvent("event2");

        ArrayList<String> ids = user.getSigned_up_events();
        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertTrue(ids.contains("event1"));
        assertTrue(ids.contains("event2"));
    }

    @Test
    public void moveToHistory_movesEventWhenPresent() {
        RegisteredUser user = createUserForLogicTest();

        ArrayList<String> signedUp = new ArrayList<>();
        signedUp.add("event1");
        signedUp.add("event2");
        user.setSigned_up_events(signedUp);

        // ensure event_history is not null
        user.setEvent_history(new HashMap<>());

        boolean result = user.moveToHistory("event1", RegisteredUser.STATUS_ACCEPTED);

        assertTrue("moveToHistory should return true when event is present", result);
        assertFalse("event should be removed from signed_up_events",
                user.getSigned_up_events().contains("event1"));
        assertEquals("History should contain the event with given status",
                RegisteredUser.STATUS_ACCEPTED,
                user.getEventStatus("event1"));
        assertTrue("History should report event is in history",
                user.isEventInHistory("event1"));
    }

    @Test
    public void moveToHistory_returnsFalseWhenEventAbsent() {
        RegisteredUser user = createUserForLogicTest();

        user.setSigned_up_events(new ArrayList<>());
        user.setEvent_history(new HashMap<>());

        boolean result = user.moveToHistory("missingEvent", RegisteredUser.STATUS_ACCEPTED);

        assertFalse("moveToHistory should return false when event is not in signed_up_events", result);
        assertNull("History should not contain the event", user.getEventStatus("missingEvent"));
    }

    @Test
    public void leaveEvent_removesFromWaitlistAndMovesToHistory() {
        RegisteredUser user = createUserForLogicTest();
        user.setDeviceID("device123");

        ArrayList<String> signedUp = new ArrayList<>();
        signedUp.add("eventX");
        user.setSigned_up_events(signedUp);

        user.setEvent_history(new HashMap<>());

        Event event = new Event();
        event.setEventID("eventX");
        ArrayList<String> waitlist = new ArrayList<>();
        waitlist.add("device123");
        waitlist.add("otherDevice");
        event.setWaitlist(waitlist);

        boolean result = user.leaveEvent(event);

        assertTrue("leaveEvent should succeed when user is signed up", result);
        assertFalse("event should be removed from signed_up_events",
                user.getSigned_up_events().contains("eventX"));
        assertEquals("History status should be CANCELLED",
                RegisteredUser.STATUS_CANCELLED,
                user.getEventStatus("eventX"));
        assertFalse("User should be removed from event waitlist",
                event.getWaitlist().contains("device123"));
        assertTrue("Other users should remain on waitlist",
                event.getWaitlist().contains("otherDevice"));
    }

    @Test
    public void leaveEvent_returnsFalseWhenNotSignedUp() {
        RegisteredUser user = createUserForLogicTest();
        user.setSigned_up_events(new ArrayList<>());
        user.setEvent_history(new HashMap<>());

        Event event = new Event();
        event.setEventID("eventY");
        event.setWaitlist(new ArrayList<>());

        boolean result = user.leaveEvent(event);

        assertFalse("leaveEvent should return false when user is not signed up", result);
        assertNull("History should not contain the event", user.getEventStatus("eventY"));
    }

    @Test
    public void getHistoricalEventIDs_returnsCopy() {
        RegisteredUser user = createUserForLogicTest();

        Map<String, String> history = new HashMap<>();
        history.put("e1", RegisteredUser.STATUS_ACCEPTED);
        history.put("e2", RegisteredUser.STATUS_DECLINED);
        user.setEvent_history(history);

        ArrayList<String> ids = user.getHistoricalEventIDs();
        assertEquals(2, ids.size());
        assertTrue(ids.contains("e1"));
        assertTrue(ids.contains("e2"));

        // modify returned list, original map should not be affected
        ids.clear();
        ArrayList<String> idsAgain = user.getHistoricalEventIDs();
        assertEquals(2, idsAgain.size());
    }

    @Test
    public void getEventHistory_returnsCopy() {
        RegisteredUser user = createUserForLogicTest();

        Map<String, String> history = new HashMap<>();
        history.put("e1", RegisteredUser.STATUS_ACCEPTED);
        user.setEvent_history(history);

        Map<String, String> copy = user.getEventHistory();
        assertEquals(1, copy.size());
        assertEquals(RegisteredUser.STATUS_ACCEPTED, copy.get("e1"));

        copy.put("e2", RegisteredUser.STATUS_DECLINED);
        // original should not change
        assertNull(user.getEvent_history().get("e2"));
    }
}
