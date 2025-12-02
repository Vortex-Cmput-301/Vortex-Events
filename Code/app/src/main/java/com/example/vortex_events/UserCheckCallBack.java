package com.example.vortex_events;

/**
 * Callback interface used for asynchronous user lookup operations.
 * Used by DatabaseWorker methods that query Firestore and need to return results asynchronously.
 */

public interface UserCheckCallBack {

    /**
     * Called when the system has checked whether the user exists.
     *
     * @param exists true if the user exists in the database, false otherwise
     */
    void onUserChecked(boolean exists);
    /**
     * Called when a registered user has been successfully retrieved.
     *
     * @param user the RegisteredUser object returned from the database
     */
    void rUserCollected(RegisteredUser user);

    /**
     * Called when a guest user has been successfully retrieved.
     *
     * @param user the GuestUser object returned from the database
     */
    Void gUserCollected(GuestUser user);
    
}
