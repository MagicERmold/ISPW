package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.boundary.GestisciProdottiBoundary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class GestisciProdottiFXController {

    @FXML
    private ListView<ProdottoBean> productsListView;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField thresholdField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField movementQuantityField;

    @FXML
    private ListView<StatisticaVenditaMensileBean> statisticsListView;

    @FXML
    private Label messageLabel;

    private final GestisciProdottiBoundary boundary = new GestisciProdottiBoundary();

    @FXML
    private void initialize() {
        configureCells();
        productsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> populateForm(newValue));
        loadProducts();
        loadStatistics();
    }

    @FXML
    private void onBackToInventory() throws IOException {
        JavaFXApp.setRoot("inventario");
    }

    @FXML
    private void onNewProduct() {
        productsListView.getSelectionModel().clearSelection();
        populateForm(null);
        setMessage("Inserire nuovo prodotto");
    }

    @FXML
    private void onRefresh() {
        loadProducts();
        loadStatistics();
        setMessage("Dati aggiornati");
    }

    @FXML
    private void onAddProduct() {
        EsitoOperazioneBean esito = boundary.aggiungiProdotto(readProductFromForm());
        afterOperation(esito);
    }

    @FXML
    private void onUpdateProduct() {
        EsitoOperazioneBean esito = boundary.modificaProdotto(readProductFromForm());
        afterOperation(esito);
    }

    @FXML
    private void onDeleteProduct() {
        EsitoOperazioneBean esito = boundary.rimuoviProdotto(readProductFromForm());
        afterOperation(esito);
    }

    @FXML
    private void onRegisterSale() {
        ProdottoBean selected = productsListView.getSelectionModel().getSelectedItem();
        String idProdotto = selected == null ? idField.getText() : selected.getId();
        EsitoOperazioneBean esito = boundary.registraVenditaManuale(idProdotto, parseInt(movementQuantityField.getText()));
        afterOperation(esito);
    }

    @FXML
    private void onRegisterExternalPurchase() {
        ProdottoBean selected = productsListView.getSelectionModel().getSelectedItem();
        String idProdotto = selected == null ? idField.getText() : selected.getId();
        EsitoOperazioneBean esito = boundary.registraAcquistoEsterno(idProdotto, parseInt(movementQuantityField.getText()));
        afterOperation(esito);
    }

    @FXML
    private void onRefreshStatistics() {
        loadStatistics();
        setMessage("Statistiche aggiornate");
    }

    private void loadProducts() {
        List<ProdottoBean> prodotti = boundary.visualizzaProdotti();
        productsListView.setItems(FXCollections.observableArrayList(prodotti));
        if (!prodotti.isEmpty()) {
            productsListView.getSelectionModel().selectFirst();
        }
    }

    private void loadStatistics() {
        statisticsListView.setItems(FXCollections.observableArrayList(boundary.analizzaStatisticheVenditaMensili()));
    }

    private void afterOperation(EsitoOperazioneBean esito) {
        setMessage(esito.getMessaggio());
        if (esito.isSuccesso()) {
            loadProducts();
            loadStatistics();
        }
    }

    private ProdottoBean readProductFromForm() {
        return new ProdottoBean(idField.getText(), nameField.getText(), categoryField.getText(),
                parseInt(quantityField.getText()), parseInt(thresholdField.getText()), parseDecimal(priceField.getText()));
    }

    private void populateForm(ProdottoBean prodotto) {
        if (prodotto == null) {
            idField.clear();
            nameField.clear();
            categoryField.clear();
            quantityField.clear();
            thresholdField.clear();
            priceField.clear();
            movementQuantityField.clear();
            return;
        }

        idField.setText(prodotto.getId());
        nameField.setText(prodotto.getNome());
        categoryField.setText(prodotto.getCategoria());
        quantityField.setText(Integer.toString(prodotto.getQuantita()));
        thresholdField.setText(Integer.toString(prodotto.getSogliaMinima()));
        priceField.setText(prodotto.getPrezzoUnitario() == null ? "0" : prodotto.getPrezzoUnitario().toPlainString());
        movementQuantityField.clear();
    }

    private void configureCells() {
        productsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ProdottoBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item.getNome() + " | qta " + item.getQuantita() + " | soglia " + item.getSogliaMinima());
            }
        });

        statisticsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(StatisticaVenditaMensileBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item.getMese() + " | venduti " + item.getQuantitaVenduta() + " | "
                        + item.getIncassoStimato() + " EUR | top " + item.getProdottoPiuVenduto());
            }
        });
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

    private void setMessage(String message) {
        messageLabel.setText(message);
    }
}
