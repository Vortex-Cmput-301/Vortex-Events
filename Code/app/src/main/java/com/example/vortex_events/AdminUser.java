package com.example.vortex_events;

import android.content.Context;

public class AdminUser extends RegisteredUser{

    public AdminUser(Context context, String number, String email, String name) {
        super(context, number, email, name);
        this.type = "Admin";
    }
}
