package com.stocktrack.bean;

/**
 * Bean usato dalla Boundary per trasferire le credenziali verso il LoginController.
 */
public class UserBean {
    private String username;
    private String password;

    public UserBean(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Lo username non puo essere vuoto.");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La password non puo essere vuota.");
        }
        this.password = password;
    }
}
