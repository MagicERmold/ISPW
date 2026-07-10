package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginFXController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AcquistaProdottiFornitoriBoundary boundary = new AcquistaProdottiFornitoriBoundary();

    @FXML
    private void onLogin() throws IOException {
        LoginBean loginBean = new LoginBean(emailField.getText(), passwordField.getText());
        ProfiloUtenteBean profiloUtenteBean = boundary.login(loginBean);
        if (profiloUtenteBean == null) {
            messageLabel.setText("Login non riuscito");
            return;
        }
        openHome(profiloUtenteBean);
    }

    @FXML
    private void onOpenRegistration() throws IOException {
        JavaFXApp.setRoot("registrazione");
    }

    private void openHome(ProfiloUtenteBean profiloUtenteBean) throws IOException {
        if (RuoloUtente.FORNITORE.equals(profiloUtenteBean.getRuolo())) {
            JavaFXApp.setRoot("inventario_fornitore");
            return;
        }
        JavaFXApp.setRoot("inventario");
    }
}
