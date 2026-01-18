package com.stocktrack.controller;

import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;
import java.io.IOException;
import java.util.List;

public class ManageUsersController {

    public List<User> getAllUsers() throws IOException {
        return DAOFactory.getUserDAO().getAllUsers();
    }

    public void removeUser(String username) throws IOException {
        // Impediamo di cancellare se stessi per evitare blocchi
        String current = com.stocktrack.engineering.singleton.SessionManager.getInstance().getCurrentUser().getUsername();
        if (current.equals(username)) {
            throw new IOException("Non puoi cancellare il tuo stesso utente!");
        }
        DAOFactory.getUserDAO().deleteUser(username);
    }
}