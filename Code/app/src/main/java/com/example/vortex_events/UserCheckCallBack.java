package com.example.vortex_events;

public interface UserCheckCallBack {
    void onUserChecked(boolean exists);
    void rUserCollected(RegisteredUser user);
    Void gUserCollected(GuestUser user);
    
}
