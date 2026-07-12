package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.GestisciFornitoriBoundary;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.view.fx.component.ProductCardFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

/**
 * Controller grafico della View JavaFX per Fornitori. Raccoglie gli eventi dell'interfaccia, costruisce bean e invoca le boundary; serve a mantenere FXML e dettagli grafici fuori dalla logica applicativa BCE.
 */
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
        EsitoListaBean<FornitoreBean> esito = boundary.visualizzaFornitoriConEsito();
        List<FornitoreBean> fornitori = esito.getElementi();
        suppliersListView.setItems(FXCollections.observableArrayList(fornitori));
        if (!fornitori.isEmpty()) {
            suppliersListView.getSelectionModel().selectFirst();
        } else {
            supplierProductsListView.getItems().clear();
        }
        setMessage(esito.getMessaggio());
    }

    private void loadSupplierProducts(FornitoreBean fornitoreBean) {
        if (fornitoreBean == null) {
            supplierProductsListView.getItems().clear();
            return;
        }
        EsitoListaBean<ProdottoBean> esito = boundary.visualizzaInventarioFornitoreConEsito(fornitoreBean);
        List<ProdottoBean> prodotti = esito.getElementi();
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        setMessage(esito.getMessaggio());
    }

    private void configureCells() {
        suppliersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(FornitoreBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " | codice " + item.getId());
            }
        });

        supplierProductsListView.setCellFactory(listView -> ProductCardFactory.productCell(false));
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
    }
}
