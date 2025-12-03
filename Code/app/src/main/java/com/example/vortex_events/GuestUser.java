package com.example.vortex_events;

import android.content.Context;
import android.view.View;

/**
 * Simple user type representing an anonymous/guest user of the app.
 */
public class GuestUser extends Users{

    /**
     * Create a guest user instance with the provided context.
     * @param context application or activity context
     */
    public GuestUser(Context context) {
        super(context);
        this.type = "Guest";
    };
}
