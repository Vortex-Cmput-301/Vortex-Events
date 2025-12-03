package com.example.vortex_events;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * AppNotification model class.
 */
public class AppNotification {
    String authorID;
    String notificationID;
    String title;
    String description;
    Date time_created;
    ArrayList<String> recievers;
    boolean read;


    /**
     * No-argument constructor required for Firestore serialization
     */
    public AppNotification() {
    }

    /**
     * Constructor for AppNotification.
     * @param authorID The ID of the user who created the notification.
     * @param title The title of the notification.
     * @param description The description of the notification.
     * @param token The list of tokens to send the notification to.
     */

    public AppNotification(String notification_id, String authorID, String title, String description, ArrayList<String> token) {
        this.authorID = authorID;
        this.description = description;
        this.time_created = new Date();
        this.title = title;
        this.read = false;
        this.notificationID = notification_id;
        this.recievers = token;
    }

    /**
    * Gets Author ID
    */
    public String getAuthorID() {
        return authorID;
    }

    /**
    * Sets Author ID
    */
    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    /**
    * Gets Notification ID
    */
    public String getNotificationID() {
        return notificationID;
    }

    /**
    * Sets Notification ID
    */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
    * Gets Title
    */
    public String getTitle() {
        return title;
    }

    /**
    * Sets Title
    */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
    * Gets Description
    */
    public String getDescription() {
        return description;
    }

    /**
    * Sets Description
    */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
    * Gets Time Created
    */
    public Date getTime_created() {
        return time_created;
    }

    /**
    * Sets Time Created
    */
    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    /**
    * Gets Read
    */
    public boolean isRead() {
        return read;
    }

    /**
    * Sets Read
    */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
    * Gets Recievers
    */
    public ArrayList<String> getRecievers() {
        return recievers;
    }

    /**
    * Sets Recievers
    */
    public void setRecievers(ArrayList<String> recievers) {
        this.recievers = recievers;
    }
}
