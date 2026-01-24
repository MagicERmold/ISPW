package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;

public class LoginGraphicalController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final LoginController loginLogic = new LoginController();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci username e password");
            errorLabel.setTextFill(Color.RED);
            return;
        }

        UserBean bean = new UserBean(username, password);
        try {
            boolean success = loginLogic.login(bean);
            if (success) {
                // Recuperiamo l'utente loggato per controllare il gruppo
                User currentUser = SessionManager.getInstance().getCurrentUser();

                if (currentUser.getGroupUid() == null || "null".equals(currentUser.getGroupUid())) {
                    // Se non ha un gruppo, lo mandiamo alla schermata di selezione
                    JavaFXApp.setRoot("group_selection");
                } else {
                    // Se ha già un gruppo, va alla Home
                    JavaFXApp.setRoot("home");
                }
            } else {
                errorLabel.setText("Credenziali non valide");
                errorLabel.setTextFill(Color.RED);
            }
        } catch (IOException e) {
            errorLabel.setText("Errore I/O: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
        } catch (StorageException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }

    @FXML
    private void switchToRegister() throws IOException {
        JavaFXApp.setRoot("register");
    }
}