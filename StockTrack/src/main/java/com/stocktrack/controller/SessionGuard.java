package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.exception.UnauthorizedOperationException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

/**
 * Utility interna al layer Controller per centralizzare i controlli di sessione
 * e autorizzazione richiesti dai casi d'uso applicativi.
 */
final class SessionGuard {
    private SessionGuard() {
    }

    /**
     * Verifica che esista un utente autenticato.
     *
     * @return utente presente nella sessione corrente
     * @throws StorageException se l'utente non ha ancora effettuato il login
     */
    static User requireLoggedUser() throws StorageException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new StorageException("Devi effettuare il login per continuare.");
        }
        return currentUser;
    }

    /**
     * Verifica che l'utente autenticato appartenga a un gruppo.
     *
     * @return utente autenticato con gruppo associato
     * @throws StorageException se la sessione non e valida o manca il gruppo
     */
    static User requireUserWithGroup() throws StorageException {
        User currentUser = requireLoggedUser();
        if (currentUser.getGroupId() == null || currentUser.getGroupId().isBlank()) {
            throw new StorageException("Devi prima creare o unirti a un gruppo.");
        }
        return currentUser;
    }

    /**
     * Verifica che l'utente autenticato sia amministratore del proprio gruppo.
     *
     * @return utente amministratore con gruppo associato
     * @throws StorageException se la sessione non e valida o l'utente non ha un gruppo
     * @throws UnauthorizedOperationException se l'utente non ha privilegi di amministratore
     */
    static User requireAdminWithGroup() throws StorageException {
        User currentUser = requireUserWithGroup();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedOperationException("Solo un amministratore puo eseguire questa operazione.");
        }
        return currentUser;
    }
}
