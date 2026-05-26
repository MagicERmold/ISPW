package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.engineering.factory.DAOFactory;

public class GroupController {
    private final ActivityLogController activityLogController = new ActivityLogController();

    public String createGroup() throws StorageException {
        // Recupero l'utente attuale da SessionManager
        User currentUser = SessionGuard.requireLoggedUser();
        if (currentUser.getGroupId() != null) {
            throw new StorageException("Appartieni gia a un gruppo.");
        }

        // Creo il codice del gruppo e lo imposto nell'attributo gruppo dell'utente attuale
        String newGroupId = "GROUP_" + currentUser.getUsername();
        currentUser.setGroupId(newGroupId);

        // L'utente attuale diventa ADMIN
        currentUser.setRole(Role.ADMIN);

        // Aggiorno il database
        DAOFactory.getUserDAO().updateUser(currentUser);
        activityLogController.recordActivity("GRUPPO", "ha creato il gruppo " + newGroupId);

        return newGroupId;
    }

    public void joinGroup(String groupId) throws StorageException {
        // Recupero l'utente attuale
        User currentUser = SessionGuard.requireLoggedUser();
        if (groupId == null || groupId.isBlank()) {
            throw new StorageException("ID gruppo non valido.");
        }
        if (!groupExists(groupId)) {
            throw new StorageException("Il gruppo indicato non esiste.");
        }

        // Imposto l'attributo gruppo del CURRENT USER
        currentUser.setGroupId(groupId);

        // Mi assicuro che l'utente sia USER
        currentUser.setRole(Role.USER);

        // Aggiorno in persistenza
        DAOFactory.getUserDAO().updateUser(currentUser);
        activityLogController.recordActivity("GRUPPO", "si e unito al gruppo " + groupId);
    }

    private boolean groupExists(String groupId) throws StorageException {
        for (User user : DAOFactory.getUserDAO().getAllUsers()) {
            if (groupId.equals(user.getGroupId())) {
                return true;
            }
        }
        return false;
    }
}
