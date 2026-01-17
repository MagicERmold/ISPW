package com.stocktrack.persistence.fs;

import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemUserDAO implements UserDAO {
    private static final String CSV_FILE_NAME = "users.csv";
    private static final Logger logger = Logger.getLogger(FileSystemUserDAO.class.getName());
    private final File file;

    public FileSystemUserDAO() {
        this.file = new File(CSV_FILE_NAME);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public User findUserByUsername(String username) {
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    if (parts[0].equals(username)) {
                        String group = (parts.length > 3) ? parts[3] : null; // Leggi il gruppo se c'è
                        return new User(parts[0], parts[1], Role.valueOf(parts[2]), group);
                    }
                }
            }
        } catch (IOException e) { logger.log(Level.SEVERE, "Errore lettura", e); }
        return null;
    }

    @Override
    public void saveUser(User user) {
        writeToFile(user, true); // true = append
    }

    @Override
    public void updateUser(User user) {
        // Per aggiornare, dobbiamo riscrivere il file.
        // 1. Leggi tutti gli utenti
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String group = (parts.length > 3) ? parts[3] : null;
                    users.add(new User(parts[0], parts[1], Role.valueOf(parts[2]), group));
                }
            }
        } catch (IOException e) { return; }

        // 2. Sostituisci quello modificato
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                found = true;
                break;
            }
        }

        // 3. Riscrivi tutto se trovato
        if (found) {
            try {
                // Svuota il file e riscrivi
                new FileWriter(file, false).close();
                for (User u : users) {
                    writeToFile(u, true);
                }
            } catch (IOException e) { logger.log(Level.SEVERE, "Errore update", e); }
        }
    }

    private void writeToFile(User user, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            String line = String.format("%s,%s,%s,%s",
                    user.getUsername(), user.getPassword(), user.getRole(), user.getGroupUid());
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { logger.log(Level.SEVERE, "Errore scrittura", e); }
    }
}