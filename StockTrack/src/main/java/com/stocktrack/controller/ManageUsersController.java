package com.stocktrack.controller;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersController {

    // Aggiunto 'StorageException' alla clausola throws
    public List<User> getMyGroupUsers() throws IOException, StorageException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String myGroup = currentUser.getGroupUid();

        // Questa chiamata ora può lanciare StorageException
        List<User> allUsers = DAOFactory.getUserDAO().getAllUsers();
        List<User> groupUsers = new ArrayList<>();

        for (User u : allUsers) {
            if (myGroup != null && myGroup.equals(u.getGroupUid())) {
                groupUsers.add(u);
            }
        }
        return groupUsers;
    }

    // Aggiunto 'StorageException' alla clausola throws
    public void removeUserFromMyGroup(String usernameToRemove) throws IOException, StorageException, StorageException {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Questa chiamata può lanciare StorageException
        User userToRemove = DAOFactory.getUserDAO().findUserByUsername(usernameToRemove);

        if (userToRemove == null) {
            // Possiamo usare IOException o una custom come 'UserNotFoundException'
            throw new IOException("Utente non trovato.");
        }

        if (currentUser.getGroupUid() == null || !currentUser.getGroupUid().equals(userToRemove.getGroupUid())) {
            throw new IOException("Non hai i permessi per gestire questo utente (Gruppo diverso).");
        }

        if (currentUser.getUsername().equals(usernameToRemove)) {
            throw new IOException("Non puoi rimuovere te stesso!");
        }

        // Sganciamo l'utente dal gruppo
        userToRemove.setGroupUid(null);
        userToRemove.setRole(Role.USER);

        // Questa chiamata può lanciare StorageException
        DAOFactory.getUserDAO().updateUser(userToRemove);
    }
}