package com.stocktrack.controller;

import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersController {

    public List<User> getMyGroupUsers() throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String myGroup = currentUser.getGroupUid();

        List<User> allUsers = DAOFactory.getUserDAO().getAllUsers();
        List<User> groupUsers = new ArrayList<>();

        for (User u : allUsers) {
            // Aggiungi solo se il gruppo corrisponde
            if (myGroup != null && myGroup.equals(u.getGroupUid())) {
                groupUsers.add(u);
            }
        }
        return groupUsers;
    }

    public void removeUserFromMyGroup(String usernameToRemove) throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        User userToRemove = DAOFactory.getUserDAO().findUserByUsername(usernameToRemove);

        if (userToRemove == null) {
            throw new IOException("Utente non trovato.");
        }

        // Controllo di sicurezza: L'utente da rimuovere deve essere nel mio stesso gruppo
        if (currentUser.getGroupUid() == null || !currentUser.getGroupUid().equals(userToRemove.getGroupUid())) {
            throw new IOException("Non hai i permessi per gestire questo utente (Gruppo diverso).");
        }

        // Controllo: Non rimuovere se stessi (l'Admin non può auto-cacciarsi, deve cancellare il gruppo o uscire)
        if (currentUser.getUsername().equals(usernameToRemove)) {
            throw new IOException("Non puoi rimuovere te stesso! Se vuoi uscire, usa l'opzione logout o crea una funzione 'Lascia Gruppo'.");
        }

        // --- MODIFICA CORE ---
        // Invece di cancellare l'utente, lo sganciamo dal gruppo.

        // 1. Impostiamo il gruppo a null
        userToRemove.setGroupUid(null);

        // 2. Resettiamo il ruolo a USER (così perde i privilegi finché non crea/entra in un nuovo gruppo)
        userToRemove.setRole(Role.USER);

        // 3. Aggiorniamo l'utente nel database (sia File che Memory)
        DAOFactory.getUserDAO().updateUser(userToRemove);
    }
}