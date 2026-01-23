package com.stocktrack.view.cli;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.DuplicateUserException;

import java.io.IOException;

public class LoginCLI {

    private final LoginController loginController = new LoginController();

    public void start() {
        boolean running = true;
        System.out.println("=== BENVENUTO IN STOCKTRACK ===");

        while (running) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("0. Esci");

            int input = InputHelper.readInt("Scegli un'opzione: ");

            switch (input) {
                case 1:
                    if (performLogin()) {
                        new HomeCLI().start();
                    }
                    break;
                case 2:
                    performRegister();
                    break;
                case 0:
                    System.out.println("Chiusura applicazione...");
                    running = false;
                    break;
                default:
                    System.out.println("Opzione non valida.");
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
                System.out.println("Login effettuato con successo!");
                return true;
            } else {
                System.out.println("Errore: Credenziali non valide.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Errore di sistema durante il login: " + e.getMessage());
            return false;
        }
    }

    private void performRegister( ) {
        System.out.println("\n--- REGISTRAZIONE ---");
        String username = InputHelper.readUsername("Username: ");
        String password = InputHelper.readPassword("Password: ");

        UserBean bean = new UserBean(username, password);

        try {
            loginController.register(bean);
            System.out.println("Registrazione avvenuta con successo! Ora puoi effettuare il login.");
        } catch (DuplicateUserException e) {
            System.out.println("Errore di Registrazione: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Errore di sistema: " + e.getMessage());
        }
    }
}