package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.boundary.GestisciInventarioFornitoreBoundary;
import com.stocktrack.view.fx.component.ProductCardFactory;
import com.stocktrack.view.support.ProductImageAssetStore;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
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
    private Label photoLabel;

    @FXML
    private Label messageLabel;

    private final GestisciInventarioFornitoreBoundary boundary = new GestisciInventarioFornitoreBoundary();
    private File selectedPhotoFile;

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
    }

    @FXML
    private void onNewProduct() {
        supplierProductsListView.getSelectionModel().clearSelection();
        populateForm(null);
        setMessage("Inserire nuovo prodotto fornitore");
    }

    @FXML
    private void onChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona foto prodotto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(photoLabel.getScene().getWindow());
        if (file == null) {
            return;
        }
        selectedPhotoFile = file;
        photoLabel.setText(file.getName());
    }

    @FXML
    private void onSaveProduct() {
        ProdottoBean prodottoBean = readProductFromForm();
        EsitoOperazioneBean esito = boundary.salvaProdotto(prodottoBean);
        setMessage(esito.getMessaggio());
        if (esito.isSuccesso()) {
            saveSelectedPhoto(prodottoBean);
            loadInventory();
        }
    }

    @FXML
    private void onLogout() throws IOException {
        boundary.logout();
        JavaFXApp.setRoot("login");
    }

    private void loadInventory() {
        EsitoListaBean<ProdottoBean> esito = boundary.visualizzaInventarioConEsito();
        List<ProdottoBean> prodotti = esito.getElementi();
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        summaryLabel.setText("Prodotti fornitore: " + prodotti.size());
        if (!prodotti.isEmpty()) {
            supplierProductsListView.getSelectionModel().selectFirst();
        }
        setMessage(esito.getMessaggio());
    }

    private void configureCells() {
        supplierProductsListView.setCellFactory(listView -> ProductCardFactory.productCell(false));
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
            selectedPhotoFile = null;
            photoLabel.setText("Nessuna foto selezionata");
            return;
        }

        idField.setText(prodotto.getId());
        nameField.setText(prodotto.getNome());
        categoryField.setText(prodotto.getCategoria());
        quantityField.setText(Integer.toString(prodotto.getQuantita()));
        priceField.setText(prodotto.getPrezzoUnitario() == null ? "0" : prodotto.getPrezzoUnitario().toPlainString());
        selectedPhotoFile = null;
        photoLabel.setText("Foto esistente o non selezionata");
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

    private void saveSelectedPhoto(ProdottoBean prodottoBean) {
        if (selectedPhotoFile == null) {
            return;
        }

        try {
            ProductImageAssetStore.saveProductImage(selectedPhotoFile.toPath(), prodottoBean);
            selectedPhotoFile = null;
            photoLabel.setText("Foto salvata");
            setMessage("Prodotto e foto salvati");
        } catch (IOException e) {
            setMessage("Prodotto salvato, errore salvataggio foto");
        }
    }
}
