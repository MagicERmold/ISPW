package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

/**
 * Bean di input BCE che trasporta e valida le credenziali dalla boundary al controller di autenticazione.
 */
public class LoginBean {

    private String username;
    private String password;

    public LoginBean() {
    }

    public LoginBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(username)) {
            throw new InvalidInputException("Username obbligatorio");
        }
        if (username.length() < 4) {
            throw new InvalidInputException("Username troppo corto");
        }
        if (isBlank(password)) {
            throw new InvalidInputException("Password obbligatoria");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Password troppo corta");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
