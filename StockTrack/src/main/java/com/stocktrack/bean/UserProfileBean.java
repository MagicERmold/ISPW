package com.stocktrack.bean;

/**
 * Bean di sola lettura usato per esporre alla Boundary le informazioni pubbliche
 * dell'utente, senza passare direttamente l'entity User.
 */
public class UserProfileBean {
    private final String username;
    private final String role;
    private final String groupId;
    private final boolean admin;

    public UserProfileBean(String username, String role, String groupId, boolean admin) {
        this.username = username;
        this.role = role;
        this.groupId = groupId;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean hasGroup() {
        return groupId == null || groupId.isBlank();
    }
}
