package com.stocktrack.view.fx.controller;

import com.stocktrack.controller.GroupController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;

public class GroupSelectionGraphicalController {

    @FXML private RadioButton joinRadio;
    @FXML private TextField groupIdField;

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
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ERRORE");
                    alert.setHeaderText("CAMPO NON VALIDO!");
                    alert.setContentText("L'ID non può essere vuoto...");
                    alert.showAndWait();
                    return;
                }
                groupController.joinGroup(id);
            } else {
                String newId = groupController.createGroup();
                // Potremmo mostrare un popup con il nuovo ID, ma per ora andiamo alla home
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("GRUPPO CREATO CON SUCCESSO!");
                alert.setContentText("Il tuo ID associato al gruppo è: " + newId);
                alert.showAndWait();
            }
            // Vai alla Home
            JavaFXApp.setRoot("home");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setContentText("ErroreI/O: "  + e.getMessage());
            alert.showAndWait();
        } catch (StorageException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERRORE");
            alert.setContentText("Errore nel recupero dati: " +  e.getMessage());
            alert.showAndWait();
        }
    }
}