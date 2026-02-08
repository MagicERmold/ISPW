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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManageUsersControllerTest {

    private ManageUsersController manageUsersController;
    private UserDAO userDAO;
    private final String adminUser = "adminUser";
    private final String testGroup = "AlphaTeam";

    @BeforeEach
    void setUp() throws StorageException {
        manageUsersController = new ManageUsersController();
        userDAO = DAOFactory.getUserDAO();

        SessionManager.getInstance().logout();

        safeDeleteUser(adminUser);
        safeDeleteUser("colleague");
        safeDeleteUser("outsider");
        safeDeleteUser("victim");


        User admin = new User(adminUser, "pass123", Role.ADMIN, testGroup);
        userDAO.saveUser(admin);
        SessionManager.getInstance().login(admin);
    }

    @AfterEach
    void tearDown() throws StorageException {
        SessionManager.getInstance().logout();

        safeDeleteUser(adminUser);
        safeDeleteUser("colleague");
        safeDeleteUser("outsider");
        safeDeleteUser("victim");
    }

    private void safeDeleteUser(String username) throws StorageException {
        if (userDAO.findUserByUsername(username) != null) {
            userDAO.deleteUser(username);
        }
    }


    @Test
    void testGetMyGroupUsers() throws StorageException {
        User colleague = new User("colleague", "pass", Role.USER, testGroup);
        User outsider = new User("outsider", "pass", Role.USER, "BetaTeam");

        userDAO.saveUser(colleague);
        userDAO.saveUser(outsider);

        List<User> groupUsers = manageUsersController.getMyGroupUsers();


        assertNotNull(groupUsers);
        assertEquals(2, groupUsers.size(), "Dovrebbero esserci 2 utenti nel gruppo (Admin + Collega)");

        boolean containsColleague = groupUsers.stream().anyMatch(u -> u.getUsername().equals("colleague"));
        boolean containsOutsider = groupUsers.stream().anyMatch(u -> u.getUsername().equals("outsider"));

        assertTrue(containsColleague, "La lista deve contenere il collega del gruppo.");
        assertFalse(containsOutsider, "La lista NON deve contenere utenti di altri gruppi.");
    }


    @Test
    void testRemoveUserFromMyGroupSuccess() throws StorageException {
        String victimName = "victim";
        User victim = new User(victimName, "pass", Role.USER, testGroup);
        userDAO.saveUser(victim);

        manageUsersController.removeUserFromMyGroup(victimName);

        User updatedVictim = userDAO.findUserByUsername(victimName);
        assertNotNull(updatedVictim);
        assertNull(updatedVictim.getGroupId(), "L'utente rimosso dovrebbe avere groupId null.");
        assertEquals(Role.USER, updatedVictim.getRole(), "L'utente rimosso dovrebbe tornare al ruolo USER base.");
    }

    @Test
    void testRemoveUserNotFound() {
        String nonExistentUser = "ghostUser";

        Exception exception = assertThrows(UserNotFoundException.class, () -> manageUsersController.removeUserFromMyGroup(nonExistentUser));

        assertTrue(exception.getMessage().contains("non trovato"));
    }


    @Test
    void testRemoveUserSecurityException() throws StorageException {
        String outsiderName = "outsider";
        User outsider = new User(outsiderName, "pass", Role.USER, "BetaTeam");
        userDAO.saveUser(outsider);

        assertThrows(SecurityException.class, () -> manageUsersController.removeUserFromMyGroup(outsiderName), "Dovrebbe lanciare SecurityException se si tenta di rimuovere un utente di un altro gruppo.");
    }

    @Test
    void testRemoveSelfException() {
        assertThrows(UserNotFoundException.class, () -> manageUsersController.removeUserFromMyGroup(adminUser), "Non dovrebbe essere possibile rimuovere se stessi.");
    }
}