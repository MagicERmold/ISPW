package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ActivityLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String groupId;
    private final String actionType;
    private final String description;
    private final LocalDateTime timestamp;

    public ActivityLog(String username, String groupId, String actionType, String description, LocalDateTime timestamp) {
        this.username = username;
        this.groupId = groupId;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
