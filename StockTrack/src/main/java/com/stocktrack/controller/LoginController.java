package com.stocktrack.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

/**
 * Controller applicativo per autenticazione e registrazione utenti.
 * Coordina Boundary, DAO e SessionManager mantenendo la logica di login fuori dalla UI.
 */
public class LoginController {

    /**
     * Autentica un utente e apre la sessione se le credenziali sono corrette.
     *
     * @param userBean dati inseriti dalla Boundary
     * @return true se il login riesce, false se username o password non sono validi
     * @throws StorageException se la persistenza non e disponibile
     */
    public boolean login(UserBean userBean) throws StorageException {
        // Recupero la modalità di persistenza
        UserDAO userDAO = DAOFactory.getUserDAO();

        // Recupero l'utente dal DATABASE
        User user = userDAO.findUserByUsername(userBean.getUsername());

        // Utente non trovato
        if (user == null) {
            return false;
        }

        // Password corretta: salvo la sessione
        if (userBean.getPassword().equals(user.getPassword())) {
            SessionManager.getInstance().login(user);
            return true;
        }

        // Password errata: errore
        return false;
    }

    /**
     * Registra un nuovo utente con ruolo base e apre la sessione dopo il salvataggio.
     *
     * @param userBean dati di registrazione ricevuti dalla Boundary
     * @return true se la registrazione viene completata
     * @throws DuplicateUserException se lo username e gia presente
     * @throws StorageException se la persistenza non e disponibile
     */
    public boolean register(UserBean userBean) throws DuplicateUserException, StorageException {
        // Recupero la modalità di persistenza
        UserDAO userDAO = DAOFactory.getUserDAO();

        // Controllo se l'utente è già presente nel database
        if (userDAO.findUserByUsername(userBean.getUsername()) != null) {
            throw new DuplicateUserException("L'utente '" + userBean.getUsername() + "' è già registrato.");
        }

        // Default provvisorio: USER (senza gruppo).
        // Il ruolo vero verrà definito quando entrerà/creerà un gruppo.
        User newUser = new User(userBean.getUsername(), userBean.getPassword(), Role.USER);

        // Salvo l'utente nel database
        userDAO.saveUser(newUser);

        // Apro la sessione solo dopo il salvataggio riuscito
        SessionManager.getInstance().login(newUser);

        return true;
    }
}
