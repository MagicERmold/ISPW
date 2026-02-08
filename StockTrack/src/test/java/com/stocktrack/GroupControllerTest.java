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

        User user = new User(testUsername, "password", Role.USER, null);

        DAOFactory.getUserDAO().saveUser(user);

        SessionManager.getInstance().login(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        DAOFactory.getUserDAO().deleteUser(testUsername);
        SessionManager.getInstance().logout();
    }

    @Test
    void testCreateGroupSuccess() throws Exception {
        String groupUid = groupController.createGroup();

        assertNotNull(groupUid, "Il metodo deve restituire l'ID del nuovo gruppo.");
        assertTrue(groupUid.contains(testUsername), "L'ID del gruppo dovrebbe contenere lo username di chi l'ha creato.");

        User currentUser = SessionManager.getInstance().getCurrentUser();
        assertEquals(Role.ADMIN, currentUser.getRole(), "Chi crea il gruppo deve diventare ADMIN.");
        assertEquals(groupUid, currentUser.getGroupId(), "L'utente deve avere l'ID del gruppo appena creato.");

        UserDAO userDAO = DAOFactory.getUserDAO();
        User storedUser = userDAO.findUserByUsername(testUsername);
        assertEquals(Role.ADMIN, storedUser.getRole(), "Il ruolo ADMIN deve essere persistito nel database.");
    }

    @Test
    void testJoinGroupSuccess() throws Exception {
        String targetGroupUid = "GROUP_EXISTING_123";

        groupController.joinGroup(targetGroupUid);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        assertEquals(Role.USER, currentUser.getRole(), "Chi si unisce a un gruppo deve essere USER.");
        assertEquals(targetGroupUid, currentUser.getGroupId(), "L'utente deve avere l'ID del gruppo target.");

        UserDAO userDAO = DAOFactory.getUserDAO();
        User storedUser = userDAO.findUserByUsername(testUsername);
        assertEquals(targetGroupUid, storedUser.getGroupId(), "L'ID del gruppo deve essere persistito nel database.");
    }
}