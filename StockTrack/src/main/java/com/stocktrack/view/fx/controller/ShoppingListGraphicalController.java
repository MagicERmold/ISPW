package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ShoppingListGraphicalController {

    @FXML private TableView<StockBean> shoppingTable;
    @FXML private TableColumn<StockBean, String> nameCol;
    @FXML private TableColumn<StockBean, Integer> qtyCol;
    @FXML private TableColumn<StockBean, Integer> thresholdCol;
    @FXML private TableColumn<StockBean, Integer> missingCol;

    private final ManageStockController controller = new ManageStockController();

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("soglia"));

        // Calcoliamo la colonna "Da Ordinare" al volo (Soglia - Quantità)
        missingCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getSoglia() - data.getValue().getQuantity()).asObject()
        );

        loadData();
    }

    public void loadData() {
        try {
            // Usa il metodo che avevamo già creato per filtrare i sottoscorta
            shoppingTable.setItems(FXCollections.observableArrayList(controller.getShoppingList()));
        } catch (Exception e) {
            showAlert("Impossibile calcolare la lista della spesa:\n" + e.getMessage());
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore Lista Spesa");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}