package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.view.fx.JavaFXApp; // Assicurati che punti alla tua classe main FX
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;

public class RegisterGraphicalController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label infoLabel;

    private final LoginController loginController = new LoginController();

    @FXML
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            infoLabel.setText("Compila tutti i campi!");
            infoLabel.setTextFill(Color.RED);
            return;
        }

        try {
            loginController.register(new UserBean(user, pass));
            infoLabel.setText("Registrazione OK! Torna al login.");
            infoLabel.setTextFill(Color.GREEN);
        } catch (Exception e) {
            infoLabel.setText(e.getMessage());
            infoLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void backToLogin() throws IOException {
        JavaFXApp.setRoot("login");
    }
}