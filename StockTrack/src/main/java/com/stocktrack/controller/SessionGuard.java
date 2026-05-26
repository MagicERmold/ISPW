package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.exception.UnauthorizedOperationException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

final class SessionGuard {
    private SessionGuard() {
    }

    static User requireLoggedUser() throws StorageException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new StorageException("Devi effettuare il login per continuare.");
        }
        return currentUser;
    }

    static User requireUserWithGroup() throws StorageException {
        User currentUser = requireLoggedUser();
        if (currentUser.getGroupId() == null || currentUser.getGroupId().isBlank()) {
            throw new StorageException("Devi prima creare o unirti a un gruppo.");
        }
        return currentUser;
    }

    static User requireAdminWithGroup() throws StorageException {
        User currentUser = requireUserWithGroup();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedOperationException("Solo un amministratore puo eseguire questa operazione.");
        }
        return currentUser;
    }
}
