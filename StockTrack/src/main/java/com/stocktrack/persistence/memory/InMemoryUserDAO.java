package com.stocktrack.persistence.memory;

import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO {
    // Simuliamo il database con una Mappa in memoria (RAM)
    private static final Map<String, User> usersDB = new HashMap<>();

    static {
        // Utenti di default per testare senza dover sempre registrarsi
        // Nota: group è null all'inizio
        User admin = new User("admin", "admin", Role.ADMIN, "GROUP_admin");
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
        // In una mappa, put sovrascrive se la chiave esiste già, quindi funge anche da update
        usersDB.put(user.getUsername(), user);
    }

    // --- NUOVI METODI IMPLEMENTATI PER RISOLVERE L'ERRORE ---

    @Override
    public List<User> getAllUsers() {
        // Restituisce tutti i valori della mappa come una lista
        return new ArrayList<>(usersDB.values());
    }

    @Override
    public void deleteUser(String username) {
        // Rimuove l'elemento dalla mappa usando la chiave (username)
        usersDB.remove(username);
    }
}