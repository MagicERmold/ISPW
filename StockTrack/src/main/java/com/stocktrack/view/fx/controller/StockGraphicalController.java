package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class StockGraphicalController {

    @FXML private TableView<StockBean> stockTable;
    @FXML private TableColumn<StockBean, String> colName;
    @FXML private TableColumn<StockBean, String> colCategory; // NUOVA COLONNA
    @FXML private TableColumn<StockBean, Integer> colQuantity;
    @FXML private TableColumn<StockBean, Integer> colThreshold;

    @FXML private TextField txtName;
    @FXML private TextField txtCategory; // NUOVO CAMPO
    @FXML private TextField txtQuantity;
    @FXML private TextField txtThreshold;

    @FXML private ComboBox<String> cmbFilterCategory; // NUOVO FILTRO

    private final ManageStockController controller = new ManageStockController();
    private final ObservableList<StockBean> tableData = FXCollections.observableArrayList();
    private final String t = "Tutte";

    @FXML
    public void initialize() {
        // Collegamento colonne -> attributi Bean
        colName.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category")); // Assicurati che StockBean abbia getCategory()
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("threshold"));

        stockTable.setItems(tableData);

        // Listener per il filtro: quando cambi categoria, ricarica la tabella
        cmbFilterCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadStocks());

        loadCategories(); // Carica le categorie nel menu a tendina
        loadStocks();     // Carica i dati iniziali
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    // Metodo pubblico richiamato dal HomeGraphicalController quando si cambia tab
    public void loadData() {
        loadCategories(); // Ricarica le categorie (nel caso ne siano state aggiunte altrove)
        loadStocks();     // Ricarica la tabella
    }

    private void loadCategories() {
        try {
            List<String> categories = controller.getCategories();
            // Aggiungiamo un'opzione per vedere tutto
            List<String> filterOptions = new ArrayList<>();
            filterOptions.add(t);
            filterOptions.addAll(categories);

            // Salviamo la selezione corrente per non resettarla
            String currentSelection = cmbFilterCategory.getValue();

            cmbFilterCategory.setItems(FXCollections.observableArrayList(filterOptions));

            if (currentSelection != null && filterOptions.contains(currentSelection)) {
                cmbFilterCategory.setValue(currentSelection);
            } else {
                cmbFilterCategory.setValue(t);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile caricare categorie: " + e.getMessage());
        }
    }

    private void loadStocks() {
        try {
            tableData.clear();
            List<StockBean> stocks;

            String selectedCat = cmbFilterCategory.getValue();

            // Logica di filtro
            if (selectedCat == null || selectedCat.equals(t) || selectedCat.isEmpty()) {
                stocks = controller.showAllStocks();
            } else {
                stocks = controller.getStocksByCategory(selectedCat);
            }

            tableData.addAll(stocks);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento", e.getMessage());
        }
    }

    @FXML
    private void handleAddStock() {
        try {
            String name = txtName.getText();
            String cat = txtCategory.getText(); // Leggiamo la categoria
            int qty = Integer.parseInt(txtQuantity.getText());
            int thr = Integer.parseInt(txtThreshold.getText());

            if (name.isEmpty() || cat.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Nome e Categoria sono obbligatori.");
                return;
            }

            // Usiamo il costruttore aggiornato del Bean
            StockBean newStock = new StockBean(name, qty, thr, cat);
            controller.addStock(newStock);

            // Pulizia campi
            txtName.clear();
            txtCategory.clear();
            txtQuantity.clear();
            txtThreshold.clear();

            // Aggiorna categorie e tabella
            loadCategories();
            // Seleziona la nuova categoria o "Tutte" per mostrare l'inserimento
            cmbFilterCategory.setValue(t);
            loadStocks();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Input", "Quantità e Soglia devono essere numeri interi.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore ", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteStock() {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selezione mancante", "Seleziona un prodotto da rimuovere.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare " + selected.getNome() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    controller.deleteStock(selected.getNome());
                    loadStocks(); // Ricarica tabella
                    loadCategories(); // Potrebbe essere sparita una categoria
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Errore  ", e.getMessage());
                }
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

    public StockBean getSelectedStock() {
        if (stockTable != null) {
            return stockTable.getSelectionModel().getSelectedItem();
        }
        return null;
    }
}