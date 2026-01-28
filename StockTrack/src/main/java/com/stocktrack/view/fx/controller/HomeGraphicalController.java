package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;

public class HomeGraphicalController {

    @FXML private Label welcomeLabel;
    @FXML private Tab usersTab;
    @FXML private TabPane mainTabPane;

    // INIEZIONE AUTOMATICA DEI SOTTO-CONTROLLER
    // Il nome deve essere: fx:id dell'include + "Controller"
    @FXML private StockGraphicalController stockViewController;
    @FXML private ShoppingListGraphicalController shoppingViewController;
    @FXML private UsersGraphicalController usersViewController;

    private final ManageStockController stockController = new ManageStockController();

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Ciao, " + user.getUsername());

            // Nascondi tab utenti se non è admin
            if (user.getRole() != Role.ADMIN) {
                mainTabPane.getTabs().remove(usersTab);
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
    private void handleLogout() throws IOException {
        SessionManager.getInstance().logout();
        JavaFXApp.setRoot("login");
    }

    @FXML
    private void handlePurchase() {
        updateStockQuantity("Acquisto", 1);
    }

    @FXML
    private void handleConsumption() {
        updateStockQuantity("Consumo", -1);
    }

    private void updateStockQuantity(String operationType, int sign) {
        // 1. Recupera il prodotto selezionato dal sotto-controller
        StockBean selectedItem = stockViewController.getSelectedStock();

        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Nessuna selezione",
                    "Seleziona un prodotto dalla tabella 'Magazzino' per procedere.");
            return;
        }

        // 2. Chiedi la quantità all'utente
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Registra " + operationType);
        dialog.setHeaderText("Prodotto: " + selectedItem.getNome());
        dialog.setContentText("Inserisci la quantità:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(qtyString -> {
            try {
                int quantity = Integer.parseInt(qtyString);
                if (quantity <= 0) throw new NumberFormatException();

                // 3. Chiama la logica di business

                stockController.modifyQuantity(selectedItem.getNome(), quantity*sign);


                // 4. Aggiorna le viste
                refreshTabs();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un numero intero valido maggiore di 0.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Errore", e.getMessage());
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}