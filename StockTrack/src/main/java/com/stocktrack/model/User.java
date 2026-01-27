package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Consigliato per evitare problemi di versione

    private final String username;
    private final String password;
    private Role role;
    private String groupId;

    public User(String username, String password, Role role) {

        // Aggiungi le 4 righe invece di questa cagata
        this(username, password, role, null);
    }

    public User(String username, String password, Role role, String groupId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groupId = "null".equals(groupId) ? null : groupId;
    }

    // GETTER e SETTER necessari
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}