package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

        UserBean bean = new UserBean(user, pass);

        try {
            loginController.register(bean);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Registrazione completata! Accesso in corso...");
            alert.showAndWait();

            boolean loginSuccess = loginController.login(bean);

            if (loginSuccess) {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser.getGroupUid() == null || "null".equals(currentUser.getGroupUid())) {
                    JavaFXApp.setRoot("group_selection");
                } else {
                    JavaFXApp.setRoot("home");
                }
            } else {
                JavaFXApp.setRoot("login");
            }
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