package com.stocktrack.persistence.memory;

import com.stocktrack.engineering.exception.StorageException;
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
        // Solamente per test
        // Utente Admin del gruppo "GROUP_admin"
        User admin = new User("admin", "admin", Role.ADMIN, "GROUP_admin");
        usersDB.put(admin.getUsername(), admin);

        // Utente User che appartiene allo STESSO GRUPPO (così possiamo testare la gestione)
        User user = new User("user", "user", Role.USER, "GROUP_admin");
        usersDB.put(user.getUsername(), user);

        // Utente esterno (per verificare che NON venga visto dall'admin)
        User outsider = new User("straniero", "123", Role.ADMIN, "GROUP_altro");
        usersDB.put(outsider.getUsername(), outsider);
    }

    @Override
    public User findUserByUsername(String username) throws StorageException {
        return usersDB.get(username);
    }

    @Override
    public void saveUser(User user) throws StorageException {

        usersDB.put(user.getUsername(), user);

    }

    @Override
    public void updateUser(User user) throws StorageException {
        // In una mappa, put sovrascrive se la chiave esiste già, quindi funge anche da update
        usersDB.put(user.getUsername(), user);
    }

    @Override
    public List<User> getAllUsers() throws StorageException {
        // Restituisce tutti i valori della mappa come una lista
        return new ArrayList<>(usersDB.values());
    }

    @Override
    public void deleteUser(String username) throws StorageException {
        // Rimuove l'elemento dalla mappa usando la chiave (username)
        usersDB.remove(username);
    }
}