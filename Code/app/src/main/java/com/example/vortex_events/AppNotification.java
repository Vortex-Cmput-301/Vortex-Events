package com.example.vortex_events;

import java.util.Date;

/**
 * AppNotification model class.
 */
public class AppNotification {

    private String authorID;
    private String notificationID;
    private String title;
    private String description;
    private Date time_created;

    // No-argument constructor required for Firestore serialization
    public AppNotification() {
    }

    public AppNotification(String authorID, String description, Date time_created, String title) {
        this.authorID = authorID;
        this.description = description;
        this.time_created = time_created;
        this.title = title;
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
}
