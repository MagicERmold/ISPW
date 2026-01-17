package com.stocktrack.controller;

import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO; // Assumi che questo metodo update esista
import com.stocktrack.engineering.factory.DAOFactory;
import java.io.IOException;

public class GroupController {

    public String createGroup() throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        // Genera un ID semplice basato sul nome utente (es. "GROUP_admin")
        String newGroupUid = "GROUP_" + currentUser.getUsername();

        currentUser.setGroupUid(newGroupUid);

        // Aggiorna il DB (dovrai implementare updateUser nel DAO, o simulare riscrivendo il file)
        // Per semplicità d'esame, assumiamo che updateUser funzioni
        DAOFactory.getUserDAO().updateUser(currentUser);

        return newGroupUid;
    }

    public void joinGroup(String groupUid) throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        currentUser.setGroupUid(groupUid);
        DAOFactory.getUserDAO().updateUser(currentUser);
    }
}