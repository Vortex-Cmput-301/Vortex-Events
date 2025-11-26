package com.example.vortex_events;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

/**
 * AppNotification model class.
 */
public class AppNotification {



    // No-argument constructor required for Firestore serialization
    public AppNotification() {
    }
    String authorID;
    String notificationID;
    String title;
    String description;
    Date time_created;
    boolean read;

    public AppNotification(String authorID, String title, String description) {
        this.authorID = authorID;
        this.description = description;
        this.time_created = new Date();
        this.title = title;
        this.read = false;
        this.notificationID = UUID.randomUUID().toString();
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


}
