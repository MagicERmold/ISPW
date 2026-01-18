package com.stocktrack.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.io.IOException;

public class LoginController {

    public boolean login(UserBean userBean) throws IOException {
        UserDAO userDAO = DAOFactory.getUserDAO(); // Metodo da aggiungere alla Factory
        User user = userDAO.findUserByUsername(userBean.getUsername());

        if (user == null) {
            // Utente non trovato (in un progetto reale lanceremmo eccezione custom)
            return false;
        }

        if (user.getPassword().equals(userBean.getPassword())) {
            // Password corretta: salvo la sessione
            SessionManager.getInstance().login(user);
            return true;
        }

        // Password errata
        return false;
    }

    public void register(UserBean userBean) throws IOException, DuplicateUserException {
        UserDAO userDAO = DAOFactory.getUserDAO();

        if (userDAO.findUserByUsername(userBean.getUsername()) != null) {
            throw new DuplicateUserException("L'utente '" + userBean.getUsername() + "' è già registrato.");
        }

        // Default provvisorio: USER (senza gruppo).
        // Il ruolo vero verrà definito quando entrerà/creerà un gruppo.
        User newUser = new User(userBean.getUsername(), userBean.getPassword(), Role.USER);
        userDAO.saveUser(newUser);
    }
}