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

    // INIEZIONE AUTOMATICA DEI SOTTO-CONTROLLER
    // Il nome deve essere: fx:id dell'include + "Controller"
    @FXML private StockGraphicalController stockViewController;
    @FXML private ShoppingListGraphicalController shoppingViewController;
    @FXML private UsersGraphicalController usersViewController;

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Ciao, " + user.getUsername() + " (" + user.getRole() + ")");

            // Nascondi tab utenti se non è admin
            if (user.getRole() != Role.ADMIN) {
                usersTab.setDisable(true);
                // Opzionale: Rimuovi proprio la tab se preferisci
                // mainTabPane.getTabs().remove(usersTab);
            }
        }
    }

    @FXML
    private void refreshTabs() {
        // Ricarica i dati di TUTTE le viste quando cambi tab
        if (stockViewController != null) stockViewController.loadData();
        if (shoppingViewController != null) shoppingViewController.loadData();
        if (usersViewController != null) usersViewController.loadData();
    }

    @FXML
    private void refreshAll() {
        refreshTabs(); // Il pulsante manuale fa la stessa cosa
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionManager.getInstance().logout();
        JavaFXApp.setRoot("login");
    }
}