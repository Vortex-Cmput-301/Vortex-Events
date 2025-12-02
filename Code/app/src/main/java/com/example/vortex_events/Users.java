package com.example.vortex_events;
import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

/**
 * User class
 */
public abstract class Users {
    String deviceID;
    String type;

    /**
     * No argument constructor.
     *
     * Required for Firestore
     */
    public Users(){};

    /**
     * The android device ID for this user.
     * @return the android device for the ID
     */
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Returns the type of the user.
     *
     * @return the user type string
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the user.
     *
     * @param type the type string to assign (Guest, Organizer)
     */
    public void setType(String type) {
        this.type = type;
    }


//    public Users(){
//        // No-argument constructor
//    }

    /**
     * Creates a user and automatically assigns the deviceID using the device's Android ID.
     * @param context activity or application context used to access system services
     */
    public Users(Context context){
        @SuppressLint("HardwareIds") String userID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.deviceID = userID;
    }



}
