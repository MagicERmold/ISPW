package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;

public class StockGraphicalController {

    @FXML private TableView<StockBean> stockTable;
    @FXML private TableColumn<StockBean, String> nameCol;
    @FXML private TableColumn<StockBean, Integer> qtyCol;
    @FXML private TableColumn<StockBean, Integer> thresholdCol;

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

        loadData();
    }

    public void loadData() {
        try {
            stockTable.setItems(FXCollections.observableArrayList(controller.showAllProducts()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onConsume() { modifySelection(-1); }

    @FXML
    private void onPurchase() { modifySelection(1); }

    private void modifySelection(int amount) {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            controller.modifyQuantity(selected.getNome(), amount);
            loadData(); // Ricarica tabella
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }

    @FXML
    private void onAddProduct() {
        try {
            String name = newNameField.getText();
            int qty = Integer.parseInt(newQtyField.getText());
            int threshold = Integer.parseInt(newThresholdField.getText());

            controller.addStock(new StockBean(name, qty, threshold));
            loadData();
            newNameField.clear(); newQtyField.clear(); newThresholdField.clear();
        } catch (Exception e) {
            showAlert("Errore Inserimento", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            controller.deleteProduct(selected.getNome());
            loadData();
        } catch (Exception e) { showAlert("Errore", e.getMessage()); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}