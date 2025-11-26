package com.example.vortex_events;
import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public abstract class Users {
    String deviceID;
    String type;

    public Users(){};

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


//    public Users(){
//        // No-argument constructor
//    }

    public Users(Context context){
        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.deviceID = userID;
    }



}
