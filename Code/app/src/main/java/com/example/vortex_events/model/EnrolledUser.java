package com.example.vortex_events.model;

import com.google.firebase.Timestamp;

public class EnrolledUser {
    public final String id;
    public final String name;       // 可能为 null，UI 会兜底显示 id
    public final Timestamp enrolledAt;

    public EnrolledUser(String id, String name, Timestamp enrolledAt) {
        this.id = id;
        this.name = name;
        this.enrolledAt = enrolledAt;
    }
}
