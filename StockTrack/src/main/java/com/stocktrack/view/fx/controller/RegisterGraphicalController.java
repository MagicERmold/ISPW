package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.UserBean;
import com.stocktrack.bean.UserProfileBean;
import com.stocktrack.controller.LoginController;
import com.stocktrack.controller.SessionController;
import com.stocktrack.engineering.exception.StorageException;
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
    private final SessionController sessionController = new SessionController();

    @FXML
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showWarning("Credenziali registrazione non valide", "Sono stati rilevati campi vuoti.");
            return;
        }

        if(passwordField.getText().length() < 8) {
            showWarning("Credenziali non valide", "La password deve contenere almeno 8 caratteri.");
            return;
        }

        UserBean bean = new UserBean(user, pass);

        try {
            loginController.register(bean);
            showInfo("Registrazione completata", "Accesso in corso...");
            UserProfileBean currentUser = sessionController.getCurrentUserProfile();
            if (currentUser == null || !currentUser.hasGroup()) {
                JavaFXApp.setRoot("group_selection");
            } else {
                JavaFXApp.setRoot("home");
            }
        } catch (StorageException e) {
            showWarning("Errore nel salvataggio dati", e.getMessage());
        } catch (IOException e) {
            showWarning("Errore interfaccia", "Impossibile cambiare schermata: " + e.getMessage());
        } catch (Exception e) {
            showWarning("Registrazione non riuscita", e.getMessage());
        }
    }

    @FXML
    private void backToLogin() {
        try {
            JavaFXApp.setRoot("login");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE   ");
            alert.setContentText("Errore caricamento pagina login: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("StockTrack");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("StockTrack");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
