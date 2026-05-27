package com.stocktrack.bean;

/**
 * Bean usato dalla Boundary per visualizzare un'attivita gia formattata.
 */
public class ActivityLogBean {
    private final String username;
    private final String actionType;
    private final String description;
    private final String timestamp;

    public ActivityLogBean(String username, String actionType, String description, String timestamp) {
        this.username = username;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getActionType() {
        return actionType;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
