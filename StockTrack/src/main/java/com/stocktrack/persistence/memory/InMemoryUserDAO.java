package com.stocktrack.persistence.memory;

import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO {
    private static final Map<String, User> usersDB = new HashMap<>();

    static {
        // Aggiorniamo i dati fake per includere il campo group null
        User admin = new User("admin", "admin", Role.ADMIN, null);
        usersDB.put(admin.getUsername(), admin);
        User user = new User("user", "user", Role.USER, null);
        usersDB.put(user.getUsername(), user);
    }

    @Override
    public User findUserByUsername(String username) {
        return usersDB.get(username);
    }

    @Override
    public void saveUser(User user) {
        usersDB.put(user.getUsername(), user);
    }

    @Override
    public void updateUser(User user) {
        // In memoria basta rimetterlo nella mappa (sovrascrive il vecchio)
        usersDB.put(user.getUsername(), user);
    }
}