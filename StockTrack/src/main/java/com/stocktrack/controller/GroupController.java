package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.engineering.factory.DAOFactory;

public class GroupController {

    public String createGroup() throws StorageException {
        // Recupero l'utente attuale da SessionManager
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Creo il codice del gruppo e lo imposto nell'attributo gruppo dell'utente attuale
        String newGroupId = "GROUP_" + currentUser.getUsername();
        currentUser.setGroupId(newGroupId);

        // L'utente attuale diventa ADMIN
        currentUser.setRole(Role.ADMIN);

        // Aggiorno il database
        DAOFactory.getUserDAO().updateUser(currentUser);

        return newGroupId;
    }

    public void joinGroup(String groupId) throws StorageException {
        // Recupero l'utente attuale
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Imposto l'attributo gruppo del CURRENT USER
        currentUser.setGroupId(groupId);

        // Mi assicuro che l'utente sia USER
        currentUser.setRole(Role.USER);

        // Aggiorno in persistenza
        DAOFactory.getUserDAO().updateUser(currentUser);
    }
}