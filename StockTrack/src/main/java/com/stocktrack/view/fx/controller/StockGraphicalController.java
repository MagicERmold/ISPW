package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class StockGraphicalController {

    @FXML private TableView<StockBean> stockTable;
    @FXML private TableColumn<StockBean, String> nameCol;
    @FXML private TableColumn<StockBean, Integer> qtyCol;
    @FXML private TableColumn<StockBean, Integer> thresholdCol;

    // Campi per la modifica quantità (NUOVO)
    @FXML private TextField actionQtyField;

    // Campi per nuovo prodotto
    @FXML private TextField newNameField;
    @FXML private TextField newQtyField;
    @FXML private TextField newThresholdField;

    private final ManageStockController controller = new ManageStockController();

    @FXML
    public void initialize() {
        // Collega le colonne ai campi del Bean
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("soglia"));

        stockTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                onDelete();
            }
        });

        loadData();
    }

    public void loadData() {
        try {
            stockTable.setItems(FXCollections.observableArrayList(controller.showAllProducts()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NUOVA LOGICA CONSUMA/ACQUISTA ---

    @FXML
    private void onConsume() {
        int amount = getActionAmount();
        if (amount > 0) {
            modifySelection(-amount); // Passiamo valore negativo per consumare
        }
    }

    @FXML
    private void onPurchase() {
        int amount = getActionAmount();
        if (amount > 0) {
            modifySelection(amount); // Passiamo valore positivo per acquistare
        }
    }

    // Helper per leggere la quantità dalla casella di testo
    private int getActionAmount() {
        String text = actionQtyField.getText();
        if (text == null || text.trim().isEmpty()) {
            return 1; // Default a 1 se vuoto
        }
        try {
            int val = Integer.parseInt(text);
            if (val <= 0) throw new NumberFormatException();
            return val;
        } catch (NumberFormatException e) {
            showAlert("Errore Quantità", "Inserisci un numero valido positivo nella casella 'Azione Rapida'.");
            return 0;
        }
    }

    private void modifySelection(int amountChange) {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Nessuna Selezione", "Seleziona un prodotto dalla tabella prima di cliccare.");
            return;
        }

        try {
            controller.modifyQuantity(selected.getNome(), amountChange);
            loadData(); // Ricarica tabella per vedere il numero aggiornato
            // Opzionale: Resettiamo il campo a 1 dopo l'uso
            // actionQtyField.setText("1");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }

    // --- FINE NUOVA LOGICA ---

    @FXML
    private void onAddProduct() {
        try {
            String name = newNameField.getText();
            int qty = Integer.parseInt(newQtyField.getText());
            int threshold = Integer.parseInt(newThresholdField.getText());

            controller.addStock(new StockBean(name, qty, threshold));
            loadData();
            newNameField.clear(); newQtyField.clear(); newThresholdField.clear();
        } catch (NumberFormatException e) {
            showAlert("Dati Invalidi", "Quantità e Soglia devono essere numeri interi.");
        } catch (Exception e) {
            showAlert("Errore Inserimento", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Nessuna Selezione", "Seleziona un prodotto da eliminare.");
            return;
        }
        try {
            controller.deleteProduct(selected.getNome());
            loadData();
        } catch (Exception e) { showAlert("Errore", e.getMessage()); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // O Warning
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}