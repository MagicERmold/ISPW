package com.stocktrack.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.exception.DuplicateUserException;
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

    // --- METODO AGGIORNATO ---
    // Non ritorna più boolean, ma void. Se qualcosa va storto, lancia eccezione.
    public void register(UserBean userBean) throws IOException, DuplicateUserException {
        UserDAO userDAO = DAOFactory.getUserDAO();

        // 1. Controllo duplicati
        if (userDAO.findUserByUsername(userBean.getUsername()) != null) {
            // Invece di return false, lanciamo l'eccezione specifica
            throw new DuplicateUserException("L'utente '" + userBean.getUsername() + "' è già registrato nel sistema.");
        }

        // 2. Creazione e salvataggio
        User newUser = new User(userBean.getUsername(), userBean.getPassword(), Role.USER);
        userDAO.saveUser(newUser);
    }
}