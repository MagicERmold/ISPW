package com.stocktrack;

import com.stocktrack.view.cli.AcquistaProdottiFornitoriCLI;
import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        String viewType = loadViewType();
        if ("GUI".equalsIgnoreCase(viewType)) {
            Application.launch(JavaFXApp.class, args);
        } else {
            new AcquistaProdottiFornitoriCLI().start();
        }
    }

    private static String loadViewType() {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "CLI";
            }
            properties.load(input);
            return properties.getProperty("view.type", "CLI");
        } catch (IOException e) {
            return "CLI";
        }
    }
}
