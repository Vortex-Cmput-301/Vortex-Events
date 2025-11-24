package com.example.vortex_events;

import android.content.Context;

public class AdminUser extends RegisteredUser{

    public AdminUser(Context context, String number, String email, String name, double latitute, double longitude) {
        super(context, number, email, name, latitute, longitude);
        this.type = "Admin";
    }
}
