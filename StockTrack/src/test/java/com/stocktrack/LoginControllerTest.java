package com.stocktrack;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;
    private final String testUsername = "testUser";

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        SessionManager.getInstance().logout();
    }

    @AfterEach
    void tearDown() {
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            if (userDAO.findUserByUsername(testUsername) != null) {
                userDAO.deleteUser(testUsername);
            }
        } catch (Exception e) {
            System.err.println("Errore durante la pulizia del test: " + e.getMessage());
        }
    }

    @Test
    void testRegisterSuccess() {
        UserBean newUser = new UserBean(testUsername, "password123");

        assertDoesNotThrow(() -> loginController.register(newUser), "La registrazione non dovrebbe lanciare eccezioni.");

        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            User retrievedUser = userDAO.findUserByUsername(testUsername);

            assertNotNull(retrievedUser, "L'utente dovrebbe essere presente nel DAO dopo la registrazione.");
            assertEquals(testUsername, retrievedUser.getUsername());
            assertEquals(Role.USER, retrievedUser.getRole(), "Il ruolo predefinito deve essere USER.");
            assertEquals("password123", retrievedUser.getPassword(), "La password deve essere salvata in chiaro.");
        } catch (StorageException e) {
            fail("Errore durante la verifica dei dati: " + e.getMessage());
        }
    }

    @Test
    void testLoginSuccess() throws Exception {
        UserBean userBean = new UserBean(testUsername, "password123");
        loginController.register(userBean);

        boolean loginResult = loginController.login(userBean);

        assertTrue(loginResult, "Il login dovrebbe restituire true con credenziali corrette.");

        User sessionUser = SessionManager.getInstance().getCurrentUser();
        assertNotNull(sessionUser, "L'utente dovrebbe essere salvato nel SessionManager.");
        assertEquals(testUsername, sessionUser.getUsername());
    }

    @Test
    void testDuplicateUserException() throws Exception {
        UserBean userBean = new UserBean(testUsername, "password123");

        loginController.register(userBean);

        assertThrows(DuplicateUserException.class, () -> loginController.register(userBean),
                "Dovrebbe lanciare DuplicateUserException se l'username e gia in uso.");
    }

    @Test
    void testPlainPasswordLoginWorks() throws Exception {
        UserDAO userDAO = DAOFactory.getUserDAO();
        userDAO.saveUser(new User(testUsername, "password123", Role.USER));

        boolean loginResult = loginController.login(new UserBean(testUsername, "password123"));

        assertTrue(loginResult, "Il login deve accettare la password salvata in chiaro.");
    }
}
