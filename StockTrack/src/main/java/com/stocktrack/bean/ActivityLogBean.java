package com.stocktrack.bean;

/**
 * Bean usato dalla Boundary per visualizzare un'attivita gia formattata.
 */
public class ActivityLogBean {
    private String username;
    private String actionType;
    private String description;
    private String timestamp;

    public ActivityLogBean(String username, String actionType, String description, String timestamp) {
        setUsername(username);
        setActionType(actionType);
        setDescription(description);
        setTimestamp(timestamp);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = normalizeRequiredText(username, "Lo username non puo essere vuoto.");
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = normalizeRequiredText(actionType, "Il tipo di attivita non puo essere vuoto.");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = normalizeRequiredText(description, "La descrizione non puo essere vuota.");
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = normalizeRequiredText(timestamp, "La data non puo essere vuota.");
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }
}
