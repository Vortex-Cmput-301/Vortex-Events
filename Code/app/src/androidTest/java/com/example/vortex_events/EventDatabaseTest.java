package com.example.vortex_events;

import static org.junit.Assert.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;


public class EventDatabaseTest {
    Context appContext = ApplicationProvider.getApplicationContext();

    @Test
    public void testCreateAndRetrieveUserWithLocation() throws ExecutionException, InterruptedException {
        // Initialize DatabaseWorker
        DatabaseWorker dbWorker = new DatabaseWorker();

        // Create a user with a specific location
        @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        double latitude = 53.5232;
        double longitude = -113.5263;
        RegisteredUser user = new RegisteredUser(deviceID, "1234567890", "test@test.com", "Test User", latitude, longitude);

        // Save the user to the database
        Tasks.await(dbWorker.createRegisteredUser(user));

        // Retrieve the user from the database
        RegisteredUser retrievedUser = Tasks.await(dbWorker.getUserByDeviceID(deviceID));

        // Assert that the retrieved user is not null
        assertNotNull(retrievedUser);

        // Assert that the retrieved user's location matches the saved location
        assertEquals(latitude, retrievedUser.getLatitude(), 0.001);
        assertEquals(longitude, retrievedUser.getLongitude(), 0.001);
    }
}
