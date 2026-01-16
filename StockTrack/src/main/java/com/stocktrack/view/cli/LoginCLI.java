package com.stocktrack.view.cli;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;

import java.io.IOException;
import java.util.Scanner;

public class LoginCLI {

    private final LoginController loginController = new LoginController();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== BENVENUTO IN STOCKTRACK ===");

        while (running) {
            System.out.println("\n1. Login");
            System.out.println("0. Esci");
            System.out.print("Scegli un'opzione: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    if (performLogin(scanner)) {
                        // Se il login va a buon fine, passiamo alla Home
                        HomeCLI home = new HomeCLI();
                        home.start();
                    }
                    break;
                case "0":
                    System.out.println("Chiusura applicazione...");
                    running = false;
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        }
    }

    private boolean performLogin(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

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
}