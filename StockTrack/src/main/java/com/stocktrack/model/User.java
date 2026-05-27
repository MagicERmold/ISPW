package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity che rappresenta un utente registrato nel sistema.
 * Contiene le informazioni persistenti usate da login, gruppi e autorizzazioni.
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Consigliato per evitare problemi di versione

    private final String username;
    private final String password;
    private Role role;
    private String groupId;

    public User(String username, String password, Role role) {
        this(username, password, role, null);
    }

    public User(String username, String password, Role role, String groupId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groupId = groupId == null || groupId.isBlank() || "null".equals(groupId) ? null : groupId;
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
