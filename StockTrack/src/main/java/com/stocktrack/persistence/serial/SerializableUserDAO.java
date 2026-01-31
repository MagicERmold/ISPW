package com.stocktrack.persistence.serial;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializableUserDAO implements UserDAO {
    private static final String FILE_NAME = "users.ser";
    private final File file;

    public SerializableUserDAO() throws StorageException {
        this.file = new File(FILE_NAME);
        // Se il file non esiste, salviamo una lista vuota per inizializzarlo
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }

    // Metodo helper per leggere l'intera lista
    @SuppressWarnings("unchecked")
    private List<User> loadAll() throws StorageException {
        if (!file.exists() || file.length() == 0) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Errore lettura database serializzato", e);
        }
    }

    // Metodo helper per salvare l'intera lista
    private void saveAll(List<User> users) throws StorageException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new StorageException("Errore critico salvataggio serializzato", e);
        }
    }

    @Override
    public User findUserByUsername(String username) throws StorageException {
        List<User> users = loadAll();
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveUser(User user) throws StorageException {
        List<User> users = loadAll();
        // Controllo base per evitare duplicati, opzionale se gestito a monte
        users.removeIf(u -> u.getUsername().equals(user.getUsername()));
        users.add(user);
        saveAll(users);
    }

    @Override
    public void updateUser(User user) throws StorageException {
        saveUser(user); // In una lista serializzata, save e update sono identici (sovrascrittura)
    }

    @Override
    public List<User> getAllUsers() throws StorageException {
        return loadAll();
    }

    @Override
    public void deleteUser(String username) throws StorageException {
        List<User> users = loadAll();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) {
            saveAll(users);
        }
    }
}