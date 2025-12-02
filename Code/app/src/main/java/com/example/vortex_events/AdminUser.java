package com.example.vortex_events;

import android.content.Context;

public class AdminUser extends RegisteredUser{
    /**
     * Constructor for AdminUser.
     * @param context The context.
     * @param number The phone number.
     * @param email The email.
     * @param name The name.
     * @param token The notification token.
     * @param latitute The latitude.
     * @param longitude The longitude.
     */
    public AdminUser(Context context, String number, String email, String name, String token, double latitute, double longitude) {
        super(context, number, email, name, token, latitute, longitude, true);
        this.type = "Admin";
    }
}
