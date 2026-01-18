package com.stocktrack.controller;

import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role; // Importante
import com.stocktrack.model.User;
import com.stocktrack.engineering.factory.DAOFactory;
import java.io.IOException;

public class GroupController {

    public String createGroup() throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // 1. Logica Gruppo
        String newGroupUid = "GROUP_" + currentUser.getUsername();
        currentUser.setGroupUid(newGroupUid);

        // 2. Logica Ruolo: Chi crea il gruppo diventa ADMIN
        currentUser.setRole(Role.ADMIN);

        // 3. Aggiornamento Persistenza
        DAOFactory.getUserDAO().updateUser(currentUser);

        return newGroupUid;
    }

    public void joinGroup(String groupUid) throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // 1. Logica Gruppo
        currentUser.setGroupUid(groupUid);

        // 2. Logica Ruolo: Chi si unisce diventa USER
        currentUser.setRole(Role.USER);

        // 3. Aggiornamento Persistenza
        DAOFactory.getUserDAO().updateUser(currentUser);
    }
}