package com.example.vortex_events;

import android.content.Context;
import android.view.View;

public class GuestUser extends Users{

    public GuestUser(Context context) {
        super(context);
        this.type = "Guest";
    };
}
