package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.boundary.GestisciInventarioFornitoreBoundary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class InventarioFornitoreFXController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<ProdottoBean> supplierProductsListView;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Label messageLabel;

    private final GestisciInventarioFornitoreBoundary boundary = new GestisciInventarioFornitoreBoundary();

    @FXML
    private void initialize() {
        configureCells();
        supplierProductsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> populateForm(newValue));
        FornitoreBean fornitore = boundary.visualizzaProfilo();
        titleLabel.setText("Magazzino " + safeName(fornitore.getNome()));
        loadInventory();
    }

    @FXML
    private void onRefresh() {
        loadInventory();
        setMessage("Magazzino fornitore aggiornato");
    }

    @FXML
    private void onNewProduct() {
        supplierProductsListView.getSelectionModel().clearSelection();
        populateForm(null);
        setMessage("Inserire nuovo prodotto fornitore");
    }

    @FXML
    private void onSaveProduct() {
        EsitoOperazioneBean esito = boundary.salvaProdotto(readProductFromForm());
        setMessage(esito.getMessaggio());
        if (esito.isSuccesso()) {
            loadInventory();
        }
    }

    @FXML
    private void onLogout() throws IOException {
        boundary.logout();
        JavaFXApp.setRoot("login");
    }

    private void loadInventory() {
        List<ProdottoBean> prodotti = boundary.visualizzaInventario();
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        summaryLabel.setText("Prodotti fornitore: " + prodotti.size());
        if (!prodotti.isEmpty()) {
            supplierProductsListView.getSelectionModel().selectFirst();
        }
    }

    private void configureCells() {
        supplierProductsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ProdottoBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                BigDecimal price = item.getPrezzoUnitario() == null ? BigDecimal.ZERO : item.getPrezzoUnitario();
                setText(item.getNome() + " | qta " + item.getQuantita() + " | " + price + " EUR");
            }
        });
    }

    private ProdottoBean readProductFromForm() {
        return new ProdottoBean(idField.getText(), nameField.getText(), categoryField.getText(),
                parseInt(quantityField.getText()), 0, parseDecimal(priceField.getText()));
    }

    private void populateForm(ProdottoBean prodotto) {
        if (prodotto == null) {
            idField.clear();
            nameField.clear();
            categoryField.clear();
            quantityField.clear();
            priceField.clear();
            return;
        }

        idField.setText(prodotto.getId());
        nameField.setText(prodotto.getNome());
        categoryField.setText(prodotto.getCategoria());
        quantityField.setText(Integer.toString(prodotto.getQuantita()));
        priceField.setText(prodotto.getPrezzoUnitario() == null ? "0" : prodotto.getPrezzoUnitario().toPlainString());
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.valueOf(-1);
        }
    }

    private String safeName(String value) {
        return value == null || value.isBlank() ? "fornitore" : value;
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
    }
}
