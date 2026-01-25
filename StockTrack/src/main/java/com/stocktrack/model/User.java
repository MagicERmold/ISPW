package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Consigliato per evitare problemi di versione

    private final String username;
    private final String password;
    private Role role;
    private String groupUid;

    public User(String username, String password, Role role) {

        this(username, password, role, null);
    }

    public User(String username, String password, Role role, String groupUid) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groupUid = "null".equals(groupUid) ? null : groupUid;
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

    public String getGroupUid() {
        return groupUid;
    }
    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }
}