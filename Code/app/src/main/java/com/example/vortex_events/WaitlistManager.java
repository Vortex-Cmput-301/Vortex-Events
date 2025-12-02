package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight helper for managing event waitlist limits and array initialization.
 * Does not modify the existing DatabaseWorker or Events schema.
 */
public class WaitlistManager {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    /**
     * Save or update the waitlist limit for an event. 0 (or missing) means unlimited.
     * @param eventId event ID
     * @param limit waitlist capacity limit
     * @return task for the operation
     */
    public Task<Void> setWaitlistLimit(@NonNull String eventId, int limit) {
        DocumentReference settings = fs.collection("Events").document(eventId);
        Map<String, Object> data = new HashMap<>();
        data.put("waitlist_limit", limit);
        return settings.set(data, SetOptions.merge());
    }

    /**
     * Initialize accepted/waitlist/declined arrays for an event (merge mode, won't overwrite other fields).
     * @param eventId event ID
     * @return task for the operation
     */
    public Task<Void> initEventArrays(@NonNull String eventId) {
        DocumentReference eventDoc = fs.collection("Events").document(eventId);
        Map<String, Object> init = new HashMap<>();
        init.put("accepted", new ArrayList<String>());
        init.put("waitlist", new ArrayList<String>());
        init.put("declined", new ArrayList<String>());
        return eventDoc.set(init, SetOptions.merge());
    }
}
