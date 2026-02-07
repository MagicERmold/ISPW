package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginGraphicalController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final LoginController loginLogic = new LoginController();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setHeaderText("CREDENZIALI LOGIN NON VALIDE!");
            alert.setContentText("Sono stati rilevati dei campi vuoti...");
            alert.showAndWait();
            return;
        }

        UserBean bean = new UserBean(username, password);
        try {
            boolean success = loginLogic.login(bean);
            if (success) {
                // Recuperiamo l'utente loggato per controllare il gruppo
                User currentUser = SessionManager.getInstance().getCurrentUser();

                if (currentUser.getGroupId() == null || "null".equals(currentUser.getGroupId())) {
                    // Se non ha un gruppo, lo mandiamo alla schermata di selezione
                    JavaFXApp.setRoot("group_selection");
                } else {
                    // Se ha già un gruppo, va alla Home
                    JavaFXApp.setRoot("home");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERRORE ");
                alert.setHeaderText("CREDENZIALI NON VALIDE!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE  ");
            alert.setHeaderText("ERRORE I/O");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (StorageException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE   ");
            alert.setHeaderText("ERRORE NEL RECUPERO DATI: " + e.getMessage());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToRegister() throws IOException {
        JavaFXApp.setRoot("register");
    }
}