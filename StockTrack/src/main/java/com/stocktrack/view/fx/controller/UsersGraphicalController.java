package com.stocktrack.view.fx.controller;

import com.stocktrack.controller.ManageUsersController;
import com.stocktrack.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

public class UsersGraphicalController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;

    private final ManageUsersController controller = new ManageUsersController();

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                handleRemoveUser();
            }
        });

        loadData();
    }

    public void loadData() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(controller.getMyGroupUsers()));
        } catch (Exception e) {
            showAlert("Errore caricamento utenti", e.getMessage());
        }
    }

    @FXML
    private void handleRemoveUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attenzione", "Seleziona un utente da rimuovere.");
            return;
        }

        try {
            controller.removeUserFromMyGroup(selected.getUsername());
            loadData(); // Ricarica la tabella
            showAlert("Successo", "Utente rimosso dal gruppo.");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}