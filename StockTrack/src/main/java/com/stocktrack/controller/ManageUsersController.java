package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.exception.UserNotFoundException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersController {

    // ADMIN: recupera la lista di utenti che si trovano nello stesso gruppo dell'ADMIN
    public List<User> getMyGroupUsers() throws StorageException {
        // Recupero l'ADMIN e il suo gruppo associato
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String myGroup = currentUser.getGroupId();

        // Recupero tutti gli utenti associati al mio gruppo
        List<User> allUsers = DAOFactory.getUserDAO().getAllUsers();
        List<User> groupUsers = new ArrayList<>();

        for (User u : allUsers) {
            if (myGroup != null && myGroup.equals(u.getGroupId())) {
                groupUsers.add(u);
            }
        }
        return groupUsers;
    }

    // ADMIN: elimina gli utenti dal gruppo dell'ADMIN
    public void removeUserFromMyGroup(String usernameToRemove) throws StorageException{
        // Recupero l'ADMIN
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Recupero l'utente da rimuovere
        User userToRemove = DAOFactory.getUserDAO().findUserByUsername(usernameToRemove);

        // Utente non trovato, gestione eccezioni
        if (userToRemove == null) {
            throw new UserNotFoundException(usernameToRemove + " non trovato!");
        }

        if (currentUser.getGroupId() == null || !currentUser.getGroupId().equals(userToRemove.getGroupId())) {
            throw new SecurityException("Non hai i permessi per gestire questo utente.");
        }

        if (currentUser.getUsername().equals(usernameToRemove)) {
            throw new UserNotFoundException("Non puoi rimuovere te stesso!");
        }

        // Rimuovo l'utente dal gruppo
        userToRemove.setGroupId(null);
        userToRemove.setRole(Role.USER);

        // Aggiorno l'utente rimosso nel database
        DAOFactory.getUserDAO().updateUser(userToRemove);
    }
}