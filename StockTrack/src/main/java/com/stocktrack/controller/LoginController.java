package com.stocktrack.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.IOException;

public class LoginController {

    public boolean login(UserBean userBean) throws IOException, StorageException {
        // Recupero la modalità di persistenza
        UserDAO userDAO = DAOFactory.getUserDAO();

        // Recupero l'utente dal DATABASE
        User user = userDAO.findUserByUsername(userBean.getUsername());

        // Utente non trovato
        if (user == null) {
            return false;
        }

        // Password corretta: salvo la sessione
        if (user.getPassword().equals(userBean.getPassword())) {
            SessionManager.getInstance().login(user);
            return true;
        }

        // Password errata: errore
        return false;
    }

    public void register(UserBean userBean) throws IOException, DuplicateUserException, StorageException {
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
    }
}