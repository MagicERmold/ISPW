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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;
    private final String TEST_USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        // Assicuriamoci che la sessione sia pulita prima di ogni test
        SessionManager.getInstance().logout();
    }

    @AfterEach
    void tearDown() {
        // Pulizia: rimuovere l'utente creato per non influenzare altri test
        // Questo è fondamentale poiché InMemoryUserDAO usa una mappa statica
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            // Controllo se l'utente esiste prima di provare a cancellarlo per evitare errori nel teardown
            if (userDAO.findUserByUsername(TEST_USERNAME) != null) {
                userDAO.deleteUser(TEST_USERNAME);
            }
        } catch (Exception e) {
            System.err.println("Errore durante la pulizia del test: " + e.getMessage());
        }
    }

    /**
     * Test Req: Registrazione utente.
     * Verifica che un nuovo utente venga salvato correttamente con ruolo default USER.
     */
    @Test
    void testRegisterSuccess() {
        UserBean newUser = new UserBean(TEST_USERNAME, "password123");

        assertDoesNotThrow(() -> loginController.register(newUser), "La registrazione non dovrebbe lanciare eccezioni.");

        // Verifica diretta sul DAO (persistenza in-memory)
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            User retrievedUser = userDAO.findUserByUsername(TEST_USERNAME);

            assertNotNull(retrievedUser, "L'utente dovrebbe essere presente nel DAO dopo la registrazione.");
            assertEquals(TEST_USERNAME, retrievedUser.getUsername());
            assertEquals(Role.USER, retrievedUser.getRole(), "Il ruolo predefinito deve essere USER.");
        } catch (IOException | StorageException e) {
            fail("Errore durante la verifica dei dati: " + e.getMessage());
        }
    }

    /**
     * Test Req: Login utente.
     * Verifica che il login funzioni e popoli il SessionManager.
     */
    @Test
    void testLoginSuccess() throws Exception {
        // Setup: Registriamo prima l'utente
        UserBean userBean = new UserBean(TEST_USERNAME, "password123");
        loginController.register(userBean);

        // Azione: Tentativo di login
        boolean loginResult = loginController.login(userBean);

        // Verifica
        assertTrue(loginResult, "Il login dovrebbe restituire true con credenziali corrette.");

        User sessionUser = SessionManager.getInstance().getCurrentUser();
        assertNotNull(sessionUser, "L'utente dovrebbe essere salvato nel SessionManager.");
        assertEquals(TEST_USERNAME, sessionUser.getUsername());
    }

    /**
     * Test Req: Gestione Eccezioni (DuplicateUserException).
     * Verifica che non sia possibile registrare due utenti con lo stesso username.
     */
    @Test
    void testDuplicateUserException() throws Exception {
        UserBean userBean = new UserBean(TEST_USERNAME, "password123");

        // 1. Prima registrazione (deve avere successo)
        loginController.register(userBean);

        // 2. Seconda registrazione identica -> Deve lanciare DuplicateUserException
        assertThrows(DuplicateUserException.class, () -> {loginController.register(userBean);}, "Dovrebbe lanciare DuplicateUserException se l'username è già in uso.");
    }
}