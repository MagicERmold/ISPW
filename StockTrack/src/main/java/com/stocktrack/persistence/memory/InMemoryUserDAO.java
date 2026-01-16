package com.stocktrack.persistence.memory;

import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO {
    // Usiamo una Mappa per trovare velocemente l'utente dato il nome
    private static final Map<String, User> usersDB = new HashMap<>();

    static {
        // PRE-CARICHIAMO DUE UTENTI PER IL TEST
        // Admin: username="admin", password="admin"
        User admin = new User("admin", "admin", Role.ADMIN);
        usersDB.put(admin.getUsername(), admin);

        // User: username="user", password="user"
        User user = new User("user", "user", Role.USER);
        usersDB.put(user.getUsername(), user);
    }

    @Override
    public User findUserByUsername(String username) {
        return usersDB.get(username);
    }

}
