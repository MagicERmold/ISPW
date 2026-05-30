package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ShoppingListGraphicalController {

    @FXML private TableView<StockBean> shoppingTable;
    @FXML private TableColumn<StockBean, String> nameCol;
    @FXML private TableColumn<StockBean, String> categoryCol;
    @FXML private TableColumn<StockBean, Integer> qtyCol;
    @FXML private TableColumn<StockBean, Integer> thresholdCol;
    @FXML private TableColumn<StockBean, Integer> missingCol;

    @FXML private ComboBox<String> cmbFilterCategory;

    private final ManageStockController controller = new ManageStockController();

    // Lista osservabile che contiene tutti i dati originali
    private ObservableList<StockBean> masterData = FXCollections.observableArrayList();
    // Lista filtrata collegata alla tabella

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category")); // NUOVO
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold")); // Nota: nel Bean il metodo è getThreshold(), verifica che non sia getSoglia()

        // Calcolo "Da Ordinare" = Soglia - Quantità
        missingCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getMissingQuantity()).asObject()
        );
        shoppingTable.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(StockBean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("low-stock-row", "empty-stock-row");
                if (!empty && item != null) {
                    getStyleClass().add(item.isEmpty() ? "empty-stock-row" : "low-stock-row");
                }
            }
        });

        // Inizializzazione del filtro
        setupFilter();

        populateCategoryCombo(List.of());
    }

    private void setupFilter() {
        // Creiamo la FilteredList basata sui dati master
        FilteredList<StockBean> filteredData = new FilteredList<>(masterData, p -> true);
        shoppingTable.setItems(filteredData);

        // Listener per il cambio di selezione nella ComboBox
        cmbFilterCategory.valueProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(stock -> {
            // Se "Tutte le Categorie" o nullo, mostra tutto
            if (newValue == null || newValue.equals("Tutte le Categorie")) {
                return true;
            }
            // Filtra per categoria (ignora maiuscole/minuscole per sicurezza)
            return stock.getCategory().equalsIgnoreCase(newValue);
        }));
    }

    public void loadData() {
        try {
            List<StockBean> list = controller.getShoppingList();
            masterData.setAll(list);

            // Popoliamo la ComboBox con le categorie uniche presenti nella lista
            populateCategoryCombo(list);

        } catch (Exception e) {
            showAlert("Impossibile calcolare la lista della spesa:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleSimulatePurchase() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acquisto prodotti");
        alert.setHeaderText(null);
        alert.setContentText("Collegamento con i fornitori in corso...\nFunzione ancora da implementare.");
        alert.showAndWait();
    }

    private void populateCategoryCombo(List<StockBean> list) {
        // Estraiamo le categorie uniche
        List<String> categories = list.stream()
                .map(StockBean::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted()
                .toList();

        ObservableList<String> comboItems = FXCollections.observableArrayList();
        comboItems.add("Tutte le Categorie"); // Opzione di default per resettare il filtro
        comboItems.addAll(categories);

        cmbFilterCategory.setItems(comboItems);
        cmbFilterCategory.getSelectionModel().selectFirst();
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore Lista Spesa");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
