package com.example.vortex_events;

import android.content.Context;

public class AdminUser extends RegisteredUser{

    public AdminUser(Context context, String number, String email, String name, double latitute, double longitude) {
        super(context, number, email, name, latitute, longitude);
    public AdminUser(Context context, String number, String email, String name, String token) {
        super(context, number, email, name, token);
        this.type = "Admin";
    }
}
