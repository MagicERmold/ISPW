package com.stocktrack.view.cli;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.engineering.exception.StorageException;


public class LoginCLI {

    private final LoginController loginController = new LoginController();

    public void start() {
        boolean running = true;
        InputHelper.print("=== BENVENUTO IN STOCKTRACK ===");

        while (running) {
            InputHelper.print("\n1. Login");
            InputHelper.print("2. Register");
            InputHelper.print("0. Esci");

            int input = InputHelper.readInt("Scegli un'opzione: ");

            switch (input) {
                case 1:
                    if (performLogin()) {
                        HomeCLI home = new HomeCLI();
                        home.start();
                    }
                    break;
                case 2:
                    if(performRegister()){
                        HomeCLI home = new HomeCLI();
                        home.start();
                    }
                    break;
                case 0:
                    InputHelper.print("Chiusura applicazione...");
                    running = false;
                    break;
                default:
                    InputHelper.print("Opzione non valida.");
            }
        }
    }

    private boolean performLogin() {
        String username = InputHelper.readUsername("Username: ");
        String password = InputHelper.readPassword("Password: ");

        UserBean userBean = new UserBean(username, password);

        try {
            boolean success = loginController.login(userBean);
            if (success) {
                InputHelper.print("Login effettuato con successo!");
                return true;
            } else {
                InputHelper.print("Errore: Credenziali non valide.");
                return false;
            }
        } catch (StorageException e) {
            InputHelper.print("Errore di sistema durante il login: " + e.getMessage());
            return false;
        }
    }

    private boolean performRegister( ) {
        InputHelper.print("\n--- REGISTRAZIONE ---");
        String username = InputHelper.readUsername("Username: ");
        String password = InputHelper.readPassword("Password: ");

        UserBean bean = new UserBean(username, password);

        try {
            loginController.register(bean);
            InputHelper.print("Registrazione avvenuta con successo! Ora puoi effettuare il login.");
            return true;
        } catch (DuplicateUserException e) {
            InputHelper.print("Errore di Registrazione: " + e.getMessage());
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}