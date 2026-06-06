package com.stocktrack.bean;

import java.util.Locale;

/**
 * Bean usato per esporre alla Boundary le informazioni pubbliche
 * dell'utente, senza passare direttamente l'entity User.
 */
public class UserProfileBean {
    private String username;
    private String role;
    private String groupId;
    private boolean admin;

    public UserProfileBean(String username, String role, String groupId, boolean admin) {
        setUsername(username);
        setRole(role);
        setGroupId(groupId);
        setAdmin(admin);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = normalizeRequiredText(username, "Lo username non puo essere vuoto.");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = normalizeRequiredText(role, "Il ruolo non puo essere vuoto.").toUpperCase(Locale.ROOT);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null || groupId.trim().isEmpty() || "null".equalsIgnoreCase(groupId.trim())
                ? null
                : groupId.trim();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }
}
