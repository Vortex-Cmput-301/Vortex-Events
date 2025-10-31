package com.example.vortex_events;

import android.content.Context;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import androidx.test.core.app.ApplicationProvider;

public class EventDatabaseTest {
    Context appContext = ApplicationProvider.getApplicationContext();
    public Event createMockEvent(){
        Event mockup = new Event("Lebron party", "Cleveland", "", "", new Date(), new Date(), new Date(), new Date(), new ArrayList<>(), "We all love lebron", 23);
        return mockup;
    }

    public RegisteredUser createMockRegisteredUser(){
        RegisteredUser user = new RegisteredUser(appContext, "780-THE-GOAT", "elbron@gamil.com", "Lebron");
        Log.d("USERID", user.deviceID);
        return  user;
    }

    @Test
    public void TestCreateEvent(){
        Event mock = createMockEvent();
        RegisteredUser maker = createMockRegisteredUser();
        GuestUser guest = new GuestUser(appContext);
        databaseWorker worker = new databaseWorker();

        String res = worker.createEvent(maker, mock);
        Log.d("STATUS", res);
    }
}
