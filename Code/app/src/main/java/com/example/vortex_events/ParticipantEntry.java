package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Data model representing a participant entry for an event with enrollment details and status.
 */
public class ParticipantEntry {

    /**
     * Status enum representing the participation state of an entrant.
     */
    public enum Status {
        ACCEPT("accept"),
        WAITING("waiting"),
        CANCELLED("cancelled");

        public final String value;
        Status(String v) { this.value = v; }

        /**
         * Parse a status string into a Status enum value.
         * @param s status string (case insensitive)
         * @return corresponding Status enum, defaults to WAITING if unknown
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

    /**
     * Default no-arg constructor required for Firestore deserialization.
     */
    public ParticipantEntry() {}

    /**
     * Create a new ParticipantEntry with all fields.
     * @param userId user/device ID
     * @param name participant name
     * @param email participant email
     * @param enrolledAt enrollment timestamp
     * @param status participation status
     */
    public ParticipantEntry(String userId, String name, String email, long enrolledAt, Status status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.enrolledAt = enrolledAt;
        this.status = status;
    }

    /** @return user ID */
    public String getUserId() { return userId; }
    /** @return participant name */
    public String getName() { return name; }
    /** @return participant email */
    public String getEmail() { return email; }
    /** @return enrollment timestamp */
    public long getEnrolledAt() { return enrolledAt; }
    /** @return participation status */
    public Status getStatus() { return status; }

    /** @param userId user ID to set */
    public void setUserId(String userId) { this.userId = userId; }
    /** @param name name to set */
    public void setName(String name) { this.name = name; }
    /** @param email email to set */
    public void setEmail(String email) { this.email = email; }
    /** @param enrolledAt enrollment timestamp to set */
    public void setEnrolledAt(long enrolledAt) { this.enrolledAt = enrolledAt; }
    /** @param status participation status to set */
    public void setStatus(Status status) { this.status = status; }
}
