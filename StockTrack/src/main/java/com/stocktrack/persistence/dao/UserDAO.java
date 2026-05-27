package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.User;
import java.util.List;

/**
 * Contratto di persistenza per gli utenti, indipendente dalla tecnologia usata
 * per salvare i dati.
 */
public interface UserDAO {
    /**
     * Cerca un utente tramite username.
     */
    User findUserByUsername(String username) throws StorageException;

    /**
     * Salva un nuovo utente.
     */
    void saveUser(User user) throws StorageException;

    /**
     * Aggiorna i dati di un utente esistente.
     */
    void updateUser(User user) throws StorageException;

    /**
     * Restituisce tutti gli utenti presenti nella persistenza.
     */
    List<User> getAllUsers() throws StorageException;

    /**
     * Elimina un utente tramite username.
     */
    void deleteUser(String username) throws StorageException;
}
