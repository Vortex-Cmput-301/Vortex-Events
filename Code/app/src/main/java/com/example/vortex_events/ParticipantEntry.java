package com.example.vortex_events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ParticipantEntry {

    public enum Status {
        ACCEPT("accept"),
        WAITING("waiting"),
        CANCELLED("cancelled");

        public final String value;
        Status(String v) { this.value = v; }

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
