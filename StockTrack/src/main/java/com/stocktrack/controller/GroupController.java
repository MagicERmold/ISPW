package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.engineering.factory.DAOFactory;

/**
 * Controller applicativo responsabile della gestione del gruppo dell'utente corrente.
 * Crea il gruppo per un nuovo amministratore o collega un utente a un gruppo esistente.
 */
public class GroupController {
    private final ActivityLogController activityLogController = new ActivityLogController();

    /**
     * Crea un gruppo associato all'utente autenticato e promuove tale utente ad amministratore.
     *
     * @return identificativo del gruppo creato
     * @throws StorageException se la sessione non e valida, l'utente ha gia un gruppo
     *                          o la persistenza non e disponibile
     */
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

    /**
     * Collega l'utente autenticato a un gruppo gia esistente con ruolo USER.
     *
     * @param groupId identificativo del gruppo da raggiungere
     * @throws StorageException se l'id non e valido, il gruppo non esiste
     *                          o la persistenza non e disponibile
     */
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

    /**
     * Controlla l'esistenza di un gruppo cercando almeno un utente associato a tale id.
     */
    private boolean groupExists(String groupId) throws StorageException {
        for (User user : DAOFactory.getUserDAO().getAllUsers()) {
            if (groupId.equals(user.getGroupId())) {
                return true;
            }
        }
        return false;
    }
}
