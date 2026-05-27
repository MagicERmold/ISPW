package com.stocktrack.controller;

import com.stocktrack.bean.UserProfileBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.exception.UnauthorizedOperationException;
import com.stocktrack.engineering.exception.UserNotFoundException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller applicativo per la gestione degli utenti appartenenti al gruppo corrente.
 * Le operazioni sono riservate agli amministratori e restituiscono bean alla Boundary.
 */
public class ManageUsersController {
    private final ActivityLogController activityLogController = new ActivityLogController();

    /**
     * Recupera gli utenti appartenenti allo stesso gruppo dell'amministratore autenticato.
     *
     * @return lista dei profili utente del gruppo
     * @throws StorageException se la sessione non e valida o la persistenza non e disponibile
     */
    public List<UserProfileBean> getMyGroupUsers() throws StorageException {
        // Recupero l'ADMIN e il suo gruppo associato
        User currentUser = SessionGuard.requireAdminWithGroup();
        String myGroup = currentUser.getGroupId();

        // Recupero tutti gli utenti associati al mio gruppo
        List<User> allUsers = DAOFactory.getUserDAO().getAllUsers();
        List<UserProfileBean> groupUsers = new ArrayList<>();

        for (User u : allUsers) {
            if (myGroup != null && myGroup.equals(u.getGroupId())) {
                groupUsers.add(new UserProfileBean(
                        u.getUsername(),
                        u.getRole().name(),
                        u.getGroupId(),
                        u.getRole() == Role.ADMIN
                ));
            }
        }
        return groupUsers;
    }

    /**
     * Rimuove dal gruppo un utente gestito dall'amministratore corrente.
     *
     * @param usernameToRemove username dell'utente da rimuovere
     * @throws StorageException se la sessione non e valida o l'aggiornamento fallisce
     */
    public void removeUserFromMyGroup(String usernameToRemove) throws StorageException{
        // Recupero l'ADMIN
        User currentUser = SessionGuard.requireAdminWithGroup();

        // Recupero l'utente da rimuovere
        User userToRemove = DAOFactory.getUserDAO().findUserByUsername(usernameToRemove);

        // Utente non trovato, gestione eccezioni
        if (userToRemove == null) {
            throw new UserNotFoundException(usernameToRemove + " non trovato!");
        }

        if (currentUser.getGroupId() == null || !currentUser.getGroupId().equals(userToRemove.getGroupId())) {
            throw new UnauthorizedOperationException("Non hai i permessi per gestire questo utente.");
        }

        if (currentUser.getUsername().equals(usernameToRemove)) {
            throw new UserNotFoundException("Non puoi rimuovere te stesso!");
        }

        // Rimuovo l'utente dal gruppo
        userToRemove.setGroupId(null);
        userToRemove.setRole(Role.USER);

        // Aggiorno l'utente rimosso nel database
        DAOFactory.getUserDAO().updateUser(userToRemove);
        activityLogController.recordActivity("UTENTI", "ha rimosso " + usernameToRemove + " dal gruppo");
    }
}
