package com.stocktrack.persistence.fs;

import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemUserDAO implements UserDAO {
    private static final String CSV_FILE_NAME = "users.csv";
    private static final Logger logger = Logger.getLogger(FileSystemUserDAO.class.getName());
    private final File file;

    public FileSystemUserDAO() {
        this.file = new File(CSV_FILE_NAME);
        // Se il file non esiste, lo creiamo
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    logger.info("File utenti creato: " + CSV_FILE_NAME);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Impossibile creare il file utenti", e);
            }
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
                // Formato atteso: username,password,ROLE
                if (parts.length >= 3) {
                    String fileUser = parts[0];
                    if (fileUser.equals(username)) {
                        String password = parts[1];
                        // Convertiamo la stringa "ADMIN" o "USER" nell'Enum Role
                        Role role = Role.valueOf(parts[2]);

                        return new User(fileUser, password, role);
                    }
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Errore lettura utenti da file", e);
        }
        return null; // Utente non trovato
    }

    @Override
    public void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Scriviamo: username,password,RUOLO
            String line = String.format("%s,%s,%s",
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole().name()); // .name() converte l'Enum in Stringa

            writer.write(line);
            writer.newLine();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore salvataggio utente su file", e);
        }
    }
}