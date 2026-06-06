package com.stocktrack.view.cli;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.DuplicateUserException;
import com.stocktrack.engineering.exception.StorageException;


public class LoginCLI {
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
        LoginController loginController = new LoginController();
        String username = InputHelper.readUsername("Username: ");
        String password = InputHelper.readPassword("Password: ");

        try {
            UserBean userBean = new UserBean(username, password);
            boolean success = loginController.login(userBean);
            if (success) {
                InputHelper.print("Login effettuato con successo!");
                return true;
            } else {
                InputHelper.print("Errore: Credenziali non valide.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            InputHelper.print("Errore input: " + e.getMessage());
            return false;
        } catch (StorageException e) {
            InputHelper.print("Errore di sistema durante il login: " + e.getMessage());
            return false;
        }
    }

    private boolean performRegister( ) {
        LoginController loginController = new LoginController();
        InputHelper.print("\n--- REGISTRAZIONE ---");
        String username = InputHelper.readUsername("Username: ");
        String password = InputHelper.readPassword("Password: ");

        try {
            UserBean bean = new UserBean(username, password);
            loginController.register(bean);
            InputHelper.print("Registrazione avvenuta con successo! Ora puoi effettuare il login.");
            return true;
        } catch (IllegalArgumentException e) {
            InputHelper.print("Errore input: " + e.getMessage());
        } catch (DuplicateUserException e) {
            InputHelper.print("Errore di Registrazione: " + e.getMessage());
        } catch (StorageException e) {
            InputHelper.print("Errore critico di sistema (Database): " + e.getMessage());
            return false;
        }
        return false;
    }
}
