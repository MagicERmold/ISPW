package com.stocktrack.model;

public class User {
    private String username;
    private String password;
    private Role role;
    private String groupUid; // CAMPO MANCANTE AGGIUNTO

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groupUid = null;
    }

    // Costruttore completo per il DAO
    public User(String username, String password, Role role, String groupUid) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groupUid = "null".equals(groupUid) ? null : groupUid;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    // METODI MANCANTI AGGIUNTI
    public String getGroupUid() { return groupUid; }
    public void setGroupUid(String groupUid) { this.groupUid = groupUid; }
}