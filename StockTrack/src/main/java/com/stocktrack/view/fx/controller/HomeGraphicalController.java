package com.stocktrack.view.fx.controller;

import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.IOException;

public class HomeGraphicalController {

    @FXML private Label welcomeLabel;
    @FXML private Tab usersTab;
    // Iniettiamo il controller della vista inclusa per poterlo aggiornare
    @FXML private StockGraphicalController stockViewController;

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Ciao, " + user.getUsername() + " (" + user.getRole() + ")");

            // Nascondi tab utenti se non è admin
            if (user.getRole() != Role.ADMIN) {
                usersTab.setDisable(true);
            }
        }
    }

    @FXML
    private void refreshAll() {
        if (stockViewController != null) {
            stockViewController.loadData();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionManager.getInstance().logout();
        JavaFXApp.setRoot("login");
    }
}