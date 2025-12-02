package com.example.vortex_events;

/**
 * Callback interface used when checking/loading users from the database.
 */
public interface UserCheckCallBack {
    /**
     * Called when the existence check completes.
     * @param exists true if user exists
     */
    void onUserChecked(boolean exists);

    /**
     * Called when a RegisteredUser object has been retrieved.
     * @param user the retrieved RegisteredUser
     */
    void rUserCollected(RegisteredUser user);

    /**
     * Called when a GuestUser object has been retrieved.
     * @param user the retrieved GuestUser
     * @return Void placeholder (unused)
     */
    Void gUserCollected(GuestUser user);
    
}
