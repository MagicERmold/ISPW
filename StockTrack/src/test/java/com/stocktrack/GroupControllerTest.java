package com.stocktrack;

import com.stocktrack.controller.GroupController;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupControllerTest {

    private GroupController groupController;
    private final String testUsername = "groupTester";

    @BeforeEach
    void setUp() throws Exception {
        groupController = new GroupController();

        // Setup: Creiamo un utente base senza gruppo e lo logghiamo
        // Inizialmente è USER e ha groupUid null (o "null")
        User user = new User(testUsername, "password", Role.USER, null);

        // Salviamo l'utente nel DAO perché il controller chiamerà updateUser()
        DAOFactory.getUserDAO().saveUser(user);

        // Simuliamo il login
        SessionManager.getInstance().login(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulizia: Rimuoviamo l'utente di test
        DAOFactory.getUserDAO().deleteUser(testUsername);
        SessionManager.getInstance().logout();
    }

    /**
     * Test Req: Creazione Gruppo.
     * Chi crea un gruppo deve diventare ADMIN e avere un groupUid generato.
     */
    @Test
    void testCreateGroupSuccess() throws Exception {
        // Azione: L'utente crea un gruppo
        String groupUid = groupController.createGroup();

        // Verifica Output
        assertNotNull(groupUid, "Il metodo deve restituire l'ID del nuovo gruppo.");
        assertTrue(groupUid.contains(testUsername), "L'ID del gruppo dovrebbe contenere lo username di chi l'ha creato.");

        // Verifica Stato Utente in Sessione
        User currentUser = SessionManager.getInstance().getCurrentUser();
        assertEquals(Role.ADMIN, currentUser.getRole(), "Chi crea il gruppo deve diventare ADMIN.");
        assertEquals(groupUid, currentUser.getGroupId(), "L'utente deve avere l'ID del gruppo appena creato.");

        // Verifica Persistenza (DAO)
        UserDAO userDAO = DAOFactory.getUserDAO();
        User storedUser = userDAO.findUserByUsername(testUsername);
        assertEquals(Role.ADMIN, storedUser.getRole(), "Il ruolo ADMIN deve essere persistito nel database.");
    }

    /**
     * Test Req: Unione al Gruppo.
     * Chi si unisce a un gruppo esistente deve diventare (o rimanere) USER e acquisire l'ID del gruppo.
     */
    @Test
    void testJoinGroupSuccess() throws Exception {
        String targetGroupUid = "GROUP_EXISTING_123";

        // Azione: L'utente si unisce a un gruppo
        groupController.joinGroup(targetGroupUid);

        // Verifica Stato Utente in Sessione
        User currentUser = SessionManager.getInstance().getCurrentUser();
        assertEquals(Role.USER, currentUser.getRole(), "Chi si unisce a un gruppo deve essere USER.");
        assertEquals(targetGroupUid, currentUser.getGroupId(), "L'utente deve avere l'ID del gruppo target.");

        // Verifica Persistenza (DAO)
        UserDAO userDAO = DAOFactory.getUserDAO();
        User storedUser = userDAO.findUserByUsername(testUsername);
        assertEquals(targetGroupUid, storedUser.getGroupId(), "L'ID del gruppo deve essere persistito nel database.");
    }
}