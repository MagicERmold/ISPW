package com.stocktrack.persistence.fs;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileSystemUserDAO implements UserDAO {
    private static final String CSV_FILE_NAME = "users.csv";
    private static final Logger logger = Logger.getLogger(FileSystemUserDAO.class.getName());
    private final File file;

    public FileSystemUserDAO() {
        this.file = new File(CSV_FILE_NAME);
        try {
            boolean isCreated = file.createNewFile();

            if (isCreated) {
                logger.info("Nuovo file database creato: " + CSV_FILE_NAME);
            } else {
                logger.info("File database già esistente: " + CSV_FILE_NAME);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Errore critico: Impossibile creare o accedere al file " + CSV_FILE_NAME, e);
        }
    }

    @Override
    public User findUserByUsername(String username) throws StorageException {
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String group = (parts.length > 3) ? parts[3] : null;
                    return new User(parts[0], parts[1], Role.valueOf(parts[2]), group);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Errore ricerca utente", e);
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writeUserLine(writer, user);
        } catch (IOException e) {
            throw new StorageException("Errore salvataggio utente", e);
        }
    }

    @Override
    public void updateUser(User user) throws StorageException {
        List<User> users = getAllUsers(); // Riusiamo il metodo che lancia già StorageException
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(users);
        }
    }

    @Override
    public List<User> getAllUsers() throws StorageException {
        List<User> users = new ArrayList<>();
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String group = (parts.length > 3) ? parts[3] : null;
                    users.add(new User(parts[0], parts[1], Role.valueOf(parts[2]), group));
                }
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura utenti", e);
        }
        return users;
    }

    @Override
    public void deleteUser(String username) throws StorageException {
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));

        if (removed) {
            rewriteFile(users);
        }
    }

    private void rewriteFile(List<User> users) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (User u : users) {
                writeUserLine(writer, u);
            }
        } catch (IOException e) {
            throw new StorageException("Errore riscrittura utenti", e);
        }
    }

    private void writeUserLine(BufferedWriter writer, User user) throws IOException {
        String line = String.format("%s,%s,%s,%s",
                user.getUsername(), user.getPassword(), user.getRole(), user.getGroupId());
        writer.write(line);
        writer.newLine();
    }
}