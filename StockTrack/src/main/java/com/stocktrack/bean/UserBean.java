package com.stocktrack.bean;

/**
 * Bean usato dalla Boundary per trasferire le credenziali verso il LoginController.
 */
public class UserBean {
    private final String username;
    private final String password;

    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
