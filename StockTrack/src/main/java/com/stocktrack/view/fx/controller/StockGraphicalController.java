package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class StockGraphicalController {

    @FXML private TableView<StockBean> stockTable;
    @FXML private TableColumn<StockBean, String> colName;
    @FXML private TableColumn<StockBean, String> colCategory;
    @FXML private TableColumn<StockBean, Integer> colQuantity;
    @FXML private TableColumn<StockBean, Integer> colThreshold;
    @FXML private TableColumn<StockBean, String> colStatus;

    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbProductCategory;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtThreshold;

    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockProductsLabel;
    @FXML private Label emptyProductsLabel;
    @FXML private Label feedbackLabel;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterCategory;
    @FXML private ComboBox<String> cmbFilterStatus;

    private final ObservableList<StockBean> allStockData = FXCollections.observableArrayList();
    private final ObservableList<StockBean> tableData = FXCollections.observableArrayList();
    private static final String ALL = "Tutte";
    private static final String STATUS_AVAILABLE = "Disponibili";
    private static final String STATUS_LOW = "Sotto soglia";
    private static final String STATUS_EMPTY = "Esauriti";

    @FXML
    public void initialize() {
        // Collegamento colonne -> attributi Bean
        colName.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category")); // Assicurati che StockBean abbia getCategory()
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("threshold"));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(getStockStatus(data.getValue())));

        stockTable.setItems(tableData);
        stockTable.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(StockBean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("low-stock-row", "empty-stock-row");
                if (!empty && item != null) {
                    if (isEmptyStock(item)) {
                        getStyleClass().add("empty-stock-row");
                    } else if (isBelowThreshold(item)) {
                        getStyleClass().add("low-stock-row");
                    }
                }
            }
        });

        applyNumericFormatter(txtQuantity);
        applyNumericFormatter(txtThreshold);
        applyUppercaseFormatter(txtName);
        applyUppercaseFormatter(txtSearch);
        cmbProductCategory.setEditable(false);
        cmbFilterStatus.setItems(FXCollections.observableArrayList(ALL, STATUS_AVAILABLE, STATUS_LOW, STATUS_EMPTY));
        cmbFilterStatus.setValue(ALL);

        // I filtri lavorano sui dati gia caricati, evitando query inutili alla persistenza.
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cmbFilterCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cmbFilterStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        loadData(); // Carica categorie e dati iniziali
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    // Metodo pubblico richiamato dal HomeGraphicalController quando si cambia tab
    public void loadData() {
        ManageStockController controller = new ManageStockController();
        try {
            loadCategories(controller.getCategories());
            loadStocks(controller.showAllStocks());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento", e.getMessage());
        }
    }

    private void loadCategories(List<String> rawCategories) {
        List<String> categories = rawCategories.stream()
                .filter(category -> category != null && !category.isBlank())
                .map(category -> category.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .sorted()
                .toList();
        // Aggiungiamo un'opzione per vedere tutto
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add(ALL);
        filterOptions.addAll(categories);

        // Salviamo la selezione corrente per non resettarla
        String currentSelection = cmbFilterCategory.getValue();

        cmbFilterCategory.setItems(FXCollections.observableArrayList(filterOptions));

        if (currentSelection != null && filterOptions.contains(currentSelection)) {
            cmbFilterCategory.setValue(currentSelection);
        } else {
            cmbFilterCategory.setValue(ALL);
        }
        refreshProductCategories(categories);
    }

    private void refreshProductCategories(List<String> categories) {
        String currentSelection = cmbProductCategory.getValue();
        cmbProductCategory.setItems(FXCollections.observableArrayList(categories));
        if (currentSelection != null && categories.contains(currentSelection)) {
            cmbProductCategory.setValue(currentSelection);
        }
    }

    private void loadStocks(List<StockBean> stocks) {
        allStockData.setAll(stocks);
        updateDashboard();
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        String selectedCategory = cmbFilterCategory.getValue();
        String selectedStatus = cmbFilterStatus.getValue();

        tableData.setAll(allStockData.stream()
                .filter(stock -> matchesSearch(stock, searchText))
                .filter(stock -> selectedCategory == null || selectedCategory.equals(ALL)
                        || selectedCategory.equalsIgnoreCase(stock.getCategory()))
                .filter(stock -> matchesStatus(stock, selectedStatus))
                .toList());
    }

    private boolean matchesSearch(StockBean stock, String searchText) {
        if (searchText.isEmpty()) {
            return true;
        }
        String category = stock.getCategory() == null ? "" : stock.getCategory();
        return stock.getNome().toLowerCase().contains(searchText)
                || category.toLowerCase().contains(searchText);
    }

    private boolean matchesStatus(StockBean stock, String selectedStatus) {
        if (selectedStatus == null || selectedStatus.equals(ALL)) {
            return true;
        }
        return switch (selectedStatus) {
            case STATUS_AVAILABLE -> !isBelowThreshold(stock) && !isEmptyStock(stock);
            case STATUS_LOW -> isBelowThreshold(stock) && !isEmptyStock(stock);
            case STATUS_EMPTY -> isEmptyStock(stock);
            default -> true;
        };
    }

    private void updateDashboard() {
        int total = allStockData.size();
        long lowStock = allStockData.stream()
                .filter(stock -> isBelowThreshold(stock) && !isEmptyStock(stock))
                .count();
        long empty = allStockData.stream()
                .filter(this::isEmptyStock)
                .count();

        totalProductsLabel.setText(String.valueOf(total));
        lowStockProductsLabel.setText(String.valueOf(lowStock));
        emptyProductsLabel.setText(String.valueOf(empty));
    }

    @FXML
    private void handleAddStock() {
        ManageStockController controller = new ManageStockController();
        try {
            String name = txtName.getText().trim().toUpperCase(Locale.ROOT);
            String cat = cmbProductCategory.getValue();
            int qty = Integer.parseInt(txtQuantity.getText());
            int thr = Integer.parseInt(txtThreshold.getText());

            cat = cat == null ? "" : cat.trim().toUpperCase(Locale.ROOT);
            if (name.isEmpty() || cat.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Nome e Categoria sono obbligatori.");
                return;
            }

            // Usiamo il costruttore aggiornato del Bean
            StockBean newStock = new StockBean(name, qty, thr, cat);
            controller.addStock(newStock);

            // Pulizia campi
            txtName.clear();
            cmbProductCategory.setValue(null);
            txtQuantity.clear();
            txtThreshold.clear();

            // Aggiorna categorie e tabella
            loadCategories(controller.getCategories());
            // Seleziona la nuova categoria o "Tutte" per mostrare l'inserimento
            cmbFilterCategory.setValue(ALL);
            loadStocks(controller.showAllStocks());
            showFeedback("Prodotto aggiunto correttamente.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Input", "Quantità e Soglia devono essere numeri interi.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore ", e.getMessage());
        }
    }

    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuova categoria");
        dialog.setHeaderText(null);
        dialog.setContentText("Nome categoria:");
        applyUppercaseFormatter(dialog.getEditor());

        dialog.showAndWait()
                .map(String::trim)
                .map(category -> category.toUpperCase(Locale.ROOT))
                .filter(category -> !category.isEmpty())
                .ifPresent(this::addCategoryOption);
    }

    private void addCategoryOption(String category) {
        boolean alreadyPresent = cmbProductCategory.getItems().stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(category));
        if (!alreadyPresent) {
            cmbProductCategory.getItems().add(category);
        }
        cmbProductCategory.setValue(category);
        showFeedback("Categoria pronta per l'inserimento.");
    }

    @FXML
    private void handleEditThreshold() {
        StockBean selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selezione mancante", "Seleziona un prodotto da modificare.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getThreshold()));
        dialog.setTitle("Modifica soglia");
        dialog.setHeaderText(selected.getNome());
        dialog.setContentText("Nuova soglia minima:");
        applyNumericFormatter(dialog.getEditor());

        dialog.showAndWait()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .ifPresent(value -> updateSelectedThreshold(selected, value));
    }

    private void updateSelectedThreshold(StockBean selected, String thresholdText) {
        ManageStockController controller = new ManageStockController();
        try {
            int newThreshold = Integer.parseInt(thresholdText);
            controller.modifyThreshold(selected.getNome(), newThreshold);
            loadStocks(controller.showAllStocks());
            showFeedback("Soglia aggiornata correttamente.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Input", "La soglia deve essere un numero intero.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore modifica soglia", e.getMessage());
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
                ManageStockController controller = new ManageStockController();
                try {
                    controller.deleteStock(selected.getNome());
                    loadStocks(controller.showAllStocks()); // Ricarica tabella
                    loadCategories(controller.getCategories()); // Potrebbe essere sparita una categoria
                    showFeedback("Prodotto rimosso correttamente.");
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

    private void showFeedback(String message) {
        feedbackLabel.setText(message);
    }

    private boolean isBelowThreshold(StockBean stock) {
        return stock.getQuantity() < stock.getThreshold();
    }

    private boolean isEmptyStock(StockBean stock) {
        return stock.getQuantity() == 0;
    }

    private String getStockStatus(StockBean stock) {
        if (isEmptyStock(stock)) {
            return "Esaurito";
        }
        if (isBelowThreshold(stock)) {
            return "Sotto soglia";
        }
        return "Disponibile";
    }

    public StockBean getSelectedStock() {
        if (stockTable != null) {
            return stockTable.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    private void applyNumericFormatter(TextField field) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };
        field.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    private void applyUppercaseFormatter(TextInputControl field) {
        UnaryOperator<TextFormatter.Change> uppercaseFilter = change -> {
            change.setText(change.getText().toUpperCase(Locale.ROOT));
            return change;
        };
        field.setTextFormatter(new TextFormatter<>(uppercaseFilter));
    }
}
