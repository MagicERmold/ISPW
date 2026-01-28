package com.stocktrack;

import com.stocktrack.controller.ManageUsersController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.exception.UserNotFoundException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ManageUsersController.
 * Author: [Il Tuo Nome] [La Tua Matricola]
 */
class ManageUsersControllerTest {

    private ManageUsersController manageUsersController;
    private UserDAO userDAO;
    private final String ADMIN_USER = "adminUser";
    private final String TEST_GROUP = "AlphaTeam";

    @BeforeEach
    void setUp() throws IOException, StorageException {
        manageUsersController = new ManageUsersController();
        userDAO = DAOFactory.getUserDAO();

        // 1. Pulisci la sessione precedente
        SessionManager.getInstance().logout();

        // 2. Assicurati che il DB sia pulito (rimuovi utenti dei test precedenti)
        // Nota: Se usi InMemoryDAO, basta un reset, ma per sicurezza cancelliamo quelli noti
        safeDeleteUser(ADMIN_USER);
        safeDeleteUser("colleague");
        safeDeleteUser("outsider");
        safeDeleteUser("victim");

        // 3. Crea e Logga l'ADMIN (Necessario perché il controller usa SessionManager)
        User admin = new User(ADMIN_USER, "pass123", Role.ADMIN, TEST_GROUP);
        userDAO.saveUser(admin);
        SessionManager.getInstance().login(admin);
    }

    @AfterEach
    void tearDown() throws StorageException {
        SessionManager.getInstance().logout();
        // Pulizia dati
        safeDeleteUser(ADMIN_USER);
        safeDeleteUser("colleague");
        safeDeleteUser("outsider");
        safeDeleteUser("victim");
    }

    private void safeDeleteUser(String username) throws StorageException {
        if (userDAO.findUserByUsername(username) != null) {
            userDAO.deleteUser(username);
        }
    }

    /**
     * Test: getMyGroupUsers
     * Verifica che vengano restituiti solo gli utenti appartenenti allo stesso gruppo dell'admin.
     */
    @Test
    void testGetMyGroupUsers() throws IOException, StorageException {
        // Setup: Creo un collega (stesso gruppo) e un estraneo (gruppo diverso)
        User colleague = new User("colleague", "pass", Role.USER, TEST_GROUP);
        User outsider = new User("outsider", "pass", Role.USER, "BetaTeam");

        userDAO.saveUser(colleague);
        userDAO.saveUser(outsider);

        // Azione
        List<User> groupUsers = manageUsersController.getMyGroupUsers();

        // Verifica
        // La lista deve contenere l'Admin stesso e il collega, ma NON l'outsider
        assertNotNull(groupUsers);
        assertEquals(2, groupUsers.size(), "Dovrebbero esserci 2 utenti nel gruppo (Admin + Collega)");

        boolean containsColleague = groupUsers.stream().anyMatch(u -> u.getUsername().equals("colleague"));
        boolean containsOutsider = groupUsers.stream().anyMatch(u -> u.getUsername().equals("outsider"));

        assertTrue(containsColleague, "La lista deve contenere il collega del gruppo.");
        assertFalse(containsOutsider, "La lista NON deve contenere utenti di altri gruppi.");
    }

    /**
     * Test: removeUserFromMyGroup (Successo)
     * Verifica che un utente venga rimosso correttamente (GroupId -> null, Role -> USER).
     */
    @Test
    void testRemoveUserFromMyGroupSuccess() throws IOException, StorageException {
        // Setup: Utente da rimuovere
        String victimName = "victim";
        User victim = new User(victimName, "pass", Role.USER, TEST_GROUP);
        userDAO.saveUser(victim);

        // Azione
        manageUsersController.removeUserFromMyGroup(victimName);

        // Verifica su DAO
        User updatedVictim = userDAO.findUserByUsername(victimName);
        assertNotNull(updatedVictim);
        assertNull(updatedVictim.getGroupId(), "L'utente rimosso dovrebbe avere groupId null.");
        assertEquals(Role.USER, updatedVictim.getRole(), "L'utente rimosso dovrebbe tornare al ruolo USER base.");
    }

    /**
     * Test: removeUserFromMyGroup (Eccezione: Utente Non Trovato)
     */
    @Test
    void testRemoveUserNotFound() {
        String nonExistentUser = "ghostUser";

        Exception exception = assertThrows(UserNotFoundException.class, () -> manageUsersController.removeUserFromMyGroup(nonExistentUser));

        assertTrue(exception.getMessage().contains("non trovato"));
    }

    /**
     * Test: removeUserFromMyGroup (Eccezione: Security - Gruppo Diverso)
     * L'admin non può rimuovere utenti che non sono nel suo gruppo.
     */
    @Test
    void testRemoveUserSecurityException() throws StorageException {
        // Setup: Utente di un altro gruppo
        String outsiderName = "outsider";
        User outsider = new User(outsiderName, "pass", Role.USER, "BetaTeam");
        userDAO.saveUser(outsider);

        // Azione e Verifica
        assertThrows(SecurityException.class, () -> manageUsersController.removeUserFromMyGroup(outsiderName), "Dovrebbe lanciare SecurityException se si tenta di rimuovere un utente di un altro gruppo.");
    }

    /**
     * Test: removeUserFromMyGroup (Eccezione: Rimozione Se Stessi)
     * L'admin non può auto-rimuoversi dal gruppo tramite questo metodo.
     */
    @Test
    void testRemoveSelfException() {
        assertThrows(UserNotFoundException.class, () -> manageUsersController.removeUserFromMyGroup(ADMIN_USER), "Non dovrebbe essere possibile rimuovere se stessi.");
    }
}