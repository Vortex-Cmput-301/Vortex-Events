package com.example.vortex_events;

import java.sql.Time;
import java.util.Date;

public class AppNotification {
    String authorID;
    String notificationID;
    String title;
    String description;
    Date time_created;

    public AppNotification(String authorID, String description, Date time_created, String title) {
        this.authorID = authorID;
        this.description = description;
        this.time_created = time_created;
        this.title = title;
    }
}
