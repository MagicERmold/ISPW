package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role; // Importante
import com.stocktrack.model.User;
import com.stocktrack.engineering.factory.DAOFactory;
import java.io.IOException;

public class GroupController {

    public String createGroup() throws IOException, StorageException {
        // Recupero l'utente attuale da SessionManager
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Creo il codice del gruppo e lo imposto nell'attributo gruppo dell'utente attuale
        String newGroupUid = "GROUP_" + currentUser.getUsername();
        currentUser.setGroupUid(newGroupUid);

        // L'utente attuale diventa ADMIN
        currentUser.setRole(Role.ADMIN);

        // Aggiorno il database
        DAOFactory.getUserDAO().updateUser(currentUser);

        return newGroupUid;
    }

    public void joinGroup(String groupUid) throws IOException, StorageException {
        // Recupero l'utente attuale
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Imposto l'attributo gruppo del CURRENT USER
        currentUser.setGroupUid(groupUid);

        // Mi assicuro che l'utente sia USER
        currentUser.setRole(Role.USER);

        // Aggiorno in persistenza
        // Potrei volere più utenti ma in gruppi diversi
        DAOFactory.getUserDAO().updateUser(currentUser);
    }
}