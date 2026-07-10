package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.GestisciFornitoriBoundary;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class FornitoriFXController {

    @FXML
    private ListView<FornitoreBean> suppliersListView;

    @FXML
    private ListView<ProdottoBean> supplierProductsListView;

    @FXML
    private VBox addSupplierPanel;

    @FXML
    private TextField supplierCodeField;

    @FXML
    private Button addSupplierButton;

    @FXML
    private Button removeSupplierButton;

    @FXML
    private Label messageLabel;

    private final GestisciFornitoriBoundary boundary = new GestisciFornitoriBoundary();

    @FXML
    private void initialize() {
        configureRoleActions();
        configureCells();
        suppliersListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> loadSupplierProducts(newValue));
        loadSuppliers();
    }

    @FXML
    private void onBackToInventory() throws IOException {
        JavaFXApp.setRoot("inventario");
    }

    @FXML
    private void onRefresh() {
        loadSuppliers();
        setMessage("Fornitori aggiornati");
    }

    @FXML
    private void onAddSupplierByCode() {
        EsitoOperazioneBean esito = boundary.aggiungiFornitoreConCodice(supplierCodeField.getText());
        setMessage(esito.getMessaggio());
        if (esito.isSuccesso()) {
            supplierCodeField.clear();
            loadSuppliers();
        }
    }

    @FXML
    private void onRemoveSupplier() {
        FornitoreBean selected = suppliersListView.getSelectionModel().getSelectedItem();
        EsitoOperazioneBean esito = boundary.rimuoviFornitore(selected);
        setMessage(esito.getMessaggio());
        if (esito.isSuccesso()) {
            supplierProductsListView.getItems().clear();
            loadSuppliers();
        }
    }

    private void configureRoleActions() {
        boolean titolare = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> RuoloUtente.TITOLARE.equals(session.getRuolo()))
                .orElse(false);
        addSupplierPanel.setVisible(titolare);
        addSupplierPanel.setManaged(titolare);
        addSupplierButton.setDisable(!titolare);
        removeSupplierButton.setDisable(!titolare);
    }

    private void loadSuppliers() {
        List<FornitoreBean> fornitori = boundary.visualizzaFornitori();
        suppliersListView.setItems(FXCollections.observableArrayList(fornitori));
        if (!fornitori.isEmpty()) {
            suppliersListView.getSelectionModel().selectFirst();
        } else {
            supplierProductsListView.getItems().clear();
            setMessage("Nessun fornitore collegato");
        }
    }

    private void loadSupplierProducts(FornitoreBean fornitoreBean) {
        if (fornitoreBean == null) {
            supplierProductsListView.getItems().clear();
            return;
        }
        List<ProdottoBean> prodotti = boundary.visualizzaInventarioFornitore(fornitoreBean);
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        setMessage(prodotti.isEmpty() ? "Inventario fornitore non disponibile" : "Inventario fornitore caricato");
    }

    private void configureCells() {
        suppliersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(FornitoreBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " | codice " + item.getId());
            }
        });

        supplierProductsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ProdottoBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                BigDecimal prezzo = item.getPrezzoUnitario() == null ? BigDecimal.ZERO : item.getPrezzoUnitario();
                setText(item.getNome() + " | qta " + item.getQuantita() + " | " + prezzo + " EUR");
            }
        });
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
    }
}
