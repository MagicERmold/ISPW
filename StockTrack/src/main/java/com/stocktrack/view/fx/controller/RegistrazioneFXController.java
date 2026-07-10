package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.RegistrazioneBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistrazioneFXController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<RuoloUtente> roleComboBox;

    @FXML
    private Label messageLabel;

    private final AcquistaProdottiFornitoriBoundary boundary = new AcquistaProdottiFornitoriBoundary();

    @FXML
    private void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(RuoloUtente.values()));
        roleComboBox.setValue(RuoloUtente.TITOLARE);
    }

    @FXML
    private void onRegister() throws IOException {
        RegistrazioneBean registrazioneBean = new RegistrazioneBean(nameField.getText(), surnameField.getText(),
                emailField.getText(), passwordField.getText(), roleComboBox.getValue());
        ProfiloUtenteBean profiloUtenteBean = boundary.registra(registrazioneBean);
        if (profiloUtenteBean == null) {
            messageLabel.setText("Registrazione non riuscita");
            return;
        }
        openHome(profiloUtenteBean);
    }

    @FXML
    private void onBackToLogin() throws IOException {
        JavaFXApp.setRoot("login");
    }

    private void openHome(ProfiloUtenteBean profiloUtenteBean) throws IOException {
        if (RuoloUtente.FORNITORE.equals(profiloUtenteBean.getRuolo())) {
            JavaFXApp.setRoot("inventario_fornitore");
            return;
        }
        JavaFXApp.setRoot("inventario");
    }
}
