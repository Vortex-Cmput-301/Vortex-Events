package com.example.vortex_events;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class) // Initialize all the @Mock fields
public class DatabaseWorkerTest {
    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private CollectionReference mockCollectionRef;
    @Mock
    private DocumentReference mockDocRef;

    public Event createMockEvent(){
        Event mockup = new Event("Lebron party", "Cleveland", "", "", new Date(), new Date(), new Date(), new Date(), new ArrayList<>(), "We all love lebron", 23);
        return mockup;
    }

    public RegisteredUser createMockRegisteredUser(){
        RegisteredUser user = new RegisteredUser(null, "780-THE-GOAT", "elbron@gamil.com", "Lebron");
        return  user;
    }

    private DatabaseWorker worker; // worker to test

    @Before
    public void setup() {

        // return mock when the db is called
        when(mockDb.collection("Events")).thenReturn(mockCollectionRef);

        // When collection.document(any string) is called, return our mock document
        when(mockCollectionRef.document(anyString())).thenReturn(mockDocRef);

        // create worker and inject mock dependencies
        worker = new DatabaseWorker(mockDb);
    }

    @Test
    public void createEvent_success() {

        // Create an instantly successful Task (will succeed)
        Task<Void> successTask = Tasks.forResult(null);

        // when doc.set(any Event object) is called, return the successful Task (only for events)
        when(mockDocRef.set(any(Event.class))).thenReturn(successTask);

        // create dummy event and user objects
        Event testEvent = createMockEvent();
        testEvent.setName("Test Event");
        Users testMaker = createMockRegisteredUser();

        // calls the method in the setup
        Task<Void> resultTask = worker.createEvent(testMaker, testEvent);
        // Verify the task was successful
        assertTrue(resultTask.isSuccessful());
        //  verify that the set method was actually called
        verify(mockDocRef).set(testEvent);
    }

    @Test
    public void createEvent_failure() {
        // create a failed task
        Exception testException = new Exception("Test Firestore failure");
        Task<Void> failedTask = Tasks.forException(testException); // create a task that has failed

        // when doc.set(any Event object) is called, return the failed Task (because were testing
        // failure we want an error to be thrown)
        when(mockDocRef.set(any(Event.class))).thenReturn(failedTask);

        // setup dummy values
        Event testEvent = createMockEvent();
        testEvent.setName("Test Event");
        Users testMaker = createMockRegisteredUser();

        // call the method under test
        Task<Void> resultTask = worker.createEvent(testMaker, testEvent);

        // make sure task failed
        assertFalse(resultTask.isSuccessful());
        assertEquals(testException, resultTask.getException());
    }
}