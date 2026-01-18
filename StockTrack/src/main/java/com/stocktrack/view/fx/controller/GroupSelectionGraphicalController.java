package com.stocktrack.view.fx.controller;

import com.stocktrack.controller.GroupController;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;

public class GroupSelectionGraphicalController {

    @FXML private RadioButton joinRadio;
    @FXML private TextField groupIdField;
    @FXML private Label errorLabel;

    private final GroupController groupController = new GroupController();

    @FXML
    public void initialize() {
        // Mostra il campo di testo solo se "Unisciti" è selezionato
        groupIdField.visibleProperty().bind(joinRadio.selectedProperty());
    }

    @FXML
    private void handleContinue() {
        try {
            if (joinRadio.isSelected()) {
                String id = groupIdField.getText();
                if (id.isEmpty()) {
                    errorLabel.setText("Inserisci l'ID del gruppo!");
                    return;
                }
                groupController.joinGroup(id);
            } else {
                String newId = groupController.createGroup();
                // Potremmo mostrare un popup con il nuovo ID, ma per ora andiamo alla home
                System.out.println("Gruppo creato: " + newId);
            }
            // Vai alla Home
            JavaFXApp.setRoot("home");
        } catch (IOException e) {
            errorLabel.setText("Errore: " + e.getMessage());
        }
    }
}