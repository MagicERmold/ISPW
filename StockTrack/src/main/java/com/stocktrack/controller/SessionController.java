package com.stocktrack.controller;

import com.stocktrack.bean.UserProfileBean;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

/**
 * Controller applicativo dedicato alle operazioni di sessione esposte alla Boundary.
 * Le view usano questa classe per leggere il profilo dell'utente corrente senza accedere
 * direttamente al singleton di sessione o alle entity del modello.
 */
public class SessionController {
    /**
     * Restituisce il profilo dell'utente autenticato, se presente.
     *
     * @return profilo utente corrente, oppure null se nessun utente ha effettuato il login
     */
    public UserProfileBean getCurrentUserProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return new UserProfileBean(
                currentUser.getUsername(),
                currentUser.getRole().name(),
                currentUser.getGroupId(),
                currentUser.getRole() == Role.ADMIN
        );
    }

    /**
     * Chiude la sessione applicativa corrente.
     */
    public void logout() {
        SessionManager.getInstance().logout();
    }
}
