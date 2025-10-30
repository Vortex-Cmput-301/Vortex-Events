package com.example.vortex_events;
import android.content.Context;
import android.provider.Settings;

public abstract class Users {
    String deviceID;

    public Users(Context context){
        String userID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.deviceID = userID;
    }
}
