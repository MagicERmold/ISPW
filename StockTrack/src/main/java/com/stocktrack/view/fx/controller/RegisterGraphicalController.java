package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterGraphicalController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final LoginController loginController = new LoginController();

    @FXML
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setHeaderText("CREDENZIALI REGISTRAZIONE NON VALIDE!");
            alert.setContentText("Sono stati rilevati dei campi vuoti...");
            alert.showAndWait();
            return;
        }

        if(passwordField.getText().length() < 8) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setHeaderText("CREDENZIALI NON VALIDE!");
            alert.setContentText("Password troppo corta, minimo 8 caratteri...");
            alert.showAndWait();
            return;
        }

        UserBean bean = new UserBean(user, pass);

        try {
            loginController.register(bean);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText("REGISTRAZIONE COMPLETATA!");
            alert.setContentText("Accesso in corso...");
            alert.showAndWait();

            boolean loginSuccess = loginController.login(bean);

            if (loginSuccess) {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser.getGroupId() == null || "null".equals(currentUser.getGroupId())) {
                    JavaFXApp.setRoot("group_selection");
                } else {
                    JavaFXApp.setRoot("home");
                }
            } else {
                JavaFXApp.setRoot("login");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void backToLogin() {
        try {
            JavaFXApp.setRoot("login");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setContentText("Errore caricamento pagina login: " + e.getMessage());
            alert.showAndWait();
        }
    }
}