package com.stocktrack;

import com.stocktrack.view.cli.LoginCLI;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        String viewType = loadViewType();

        if ("GUI".equalsIgnoreCase(viewType)) {
            System.out.println("Avvio interfaccia grafica (GUI)...");
            Application.launch(JavaFXApp.class, args);

            System.out.println("NOTA: Collega qui la tua classe JavaFX (es. Application.launch(...))");

        } else {
            System.out.println("Avvio interfaccia testuale (CLI)...");
            LoginCLI loginCLI = new LoginCLI();
            loginCLI.start();
        }
    }

    private static String loadViewType() {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Attenzione: config.properties non trovato. Default su CLI.");
                return "CLI";
            }
            prop.load(input);
            return prop.getProperty("view.type", "CLI");
        } catch (IOException ex) {
            System.err.println("Errore lettura config: " + ex.getMessage());
            return "CLI";
        }
    }
}