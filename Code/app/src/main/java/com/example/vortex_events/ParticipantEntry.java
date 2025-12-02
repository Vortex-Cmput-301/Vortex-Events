package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/**
 * Model class representing a participant's enrollment in an event.
 */
public class ParticipantEntry {
    /**
     * Enum representing the status of a participant in an event.
     */
    public enum Status {
        ACCEPT("accept"),
        WAITING("waiting"),
        CANCELLED("cancelled");

        public final String value;
        Status(String v) { this.value = v; }


        /**
         * Maps a raw string to a Status enum value in a forgiving way.
         * Handles common variants like "waiting list", "accepted", "canceled", e
         *
         * @param s the input status string (may be null)
         * @return the corresponding Status value, or WAITING if the string is null or unrecognized
         */
        @NonNull
        public static Status from(@Nullable String s) {
            if (s == null) return WAITING;
            s = s.trim().toLowerCase();
            // "waiting list" / "waiting"
            if (s.equals("waiting") || s.equals("waiting list")) return WAITING;
            if (s.equals("accept") || s.equals("accepted")) return ACCEPT;
            if (s.equals("cancelled") || s.equals("canceled")) return CANCELLED;
            return WAITING;
        }
    }

    private String userId;
    private String name;
    private String email;
    private long enrolledAt; // timestamp
    private Status status;

    public ParticipantEntry() {}

    /**
     * Creates a new ParticipantEntry with all fields initialized.
     *
     * @param userId     the unique ID of the user
     * @param name       the participant's display name
     * @param email      the participant's email address
     * @param enrolledAt the timestamp when the participant enrolled (e.g., millis since epoch)
     * @param status     the current enrollment status
     */
    public ParticipantEntry(String userId, String name, String email, long enrolledAt, Status status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.enrolledAt = enrolledAt;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public long getEnrolledAt() { return enrolledAt; }
    public Status getStatus() { return status; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setEnrolledAt(long enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setStatus(Status status) { this.status = status; }
}
