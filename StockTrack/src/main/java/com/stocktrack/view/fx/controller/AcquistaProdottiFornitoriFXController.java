package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.view.fx.component.ProductCardFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller grafico della View JavaFX per AcquistaProdottiFornitori. Raccoglie gli eventi dell'interfaccia, costruisce bean e invoca le boundary; serve a mantenere FXML e dettagli grafici fuori dalla logica applicativa BCE.
 */
public class AcquistaProdottiFornitoriFXController {

    @FXML
    private ComboBox<FornitoreBean> supplierComboBox;

    @FXML
    private ListView<ProdottoBean> supplierProductsListView;

    @FXML
    private Button loadProductsButton;

    @FXML
    private TextField quantityField;

    @FXML
    private Button addToCartButton;

    @FXML
    private ListView<ProdottoBean> cartListView;

    @FXML
    private Label totalLabel;

    @FXML
    private RadioButton visaRadioButton;

    @FXML
    private RadioButton paypalRadioButton;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField cvvField;

    @FXML
    private TextField paypalEmailField;

    @FXML
    private Button payAndConfirmButton;

    @FXML
    private Label messageLabel;

    private final AcquistaProdottiFornitoriBoundary boundary = new AcquistaProdottiFornitoriBoundary();
    private final List<ProdottoBean> selectedProducts = new ArrayList<>();
    private final ToggleGroup paymentGroup = new ToggleGroup();
    private CarrelloBean currentCart = new CarrelloBean();

    @FXML
    private void initialize() {
        visaRadioButton.setToggleGroup(paymentGroup);
        paypalRadioButton.setToggleGroup(paymentGroup);
        visaRadioButton.setSelected(true);
        paymentGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updatePaymentFields());
        updatePaymentFields();
        configureCells();
        loadSuppliers();
        configureRoleAccess();
    }

    @FXML
    private void onLoadProducts() {
        FornitoreBean fornitoreBean = supplierComboBox.getValue();
        if (fornitoreBean == null) {
            setMessage("Selezionare un fornitore");
            return;
        }

        EsitoListaBean<ProdottoBean> esito = boundary.recuperaProdottiConEsito(fornitoreBean);
        List<ProdottoBean> prodotti = esito.getElementi();
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        setMessage(esito.getMessaggio());
    }

    @FXML
    private void onAddToCart() {
        ProdottoBean prodottoBean = supplierProductsListView.getSelectionModel().getSelectedItem();
        if (prodottoBean == null) {
            setMessage("Selezionare un prodotto");
            return;
        }

        int quantita = parseQuantity();
        if (quantita <= 0 || quantita > prodottoBean.getQuantita()) {
            setMessage("Quantita non valida");
            return;
        }

        selectedProducts.add(new ProdottoBean(prodottoBean.getId(), prodottoBean.getNome(),
                prodottoBean.getCategoria(), quantita, prodottoBean.getSogliaMinima(),
                prodottoBean.getPrezzoUnitario()));
        currentCart = boundary.configuraCarrello(selectedProducts);
        if (!currentCart.isSuccesso()) {
            setMessage(currentCart.getMessaggio());
            return;
        }
        cartListView.setItems(FXCollections.observableArrayList(currentCart.getProdotti()));
        totalLabel.setText("Totale: " + currentCart.getTotaleStimato() + " EUR");
        setMessage(currentCart.getMessaggio());
    }

    @FXML
    private void onPayAndConfirm() {
        if (currentCart.getProdotti().isEmpty()) {
            setMessage("Carrello vuoto");
            return;
        }

        EsitoPagamentoBean esitoPagamento = boundary.effettuaPagamento(creaPagamento());
        if (!esitoPagamento.isSuccesso()) {
            setMessage(esitoPagamento.getMessaggio());
            return;
        }

        OrdineBean ordineBean = new OrdineBean("ORD-" + UUID.randomUUID(), supplierComboBox.getValue(),
                currentCart.getProdotti(), currentCart.getTotaleStimato());
        EsitoOrdineBean esitoOrdine = boundary.confermaOrdine(ordineBean);
        setMessage(esitoOrdine.getMessaggio());
        if (esitoOrdine.isSuccesso()) {
            selectedProducts.clear();
            currentCart = new CarrelloBean();
            cartListView.setItems(FXCollections.observableArrayList());
            totalLabel.setText("Totale: 0 EUR");
            onLoadProducts();
        }
    }

    @FXML
    private void onBackToInventory() throws IOException {
        JavaFXApp.setRoot("inventario");
    }

    private PagamentoBean creaPagamento() {
        if (visaRadioButton.isSelected()) {
            return new PagamentoBean("VISA", cardNumberField.getText(), cvvField.getText(), null,
                    currentCart.getTotaleStimato(), "EUR");
        }
        return new PagamentoBean("PAYPAL", null, null, paypalEmailField.getText(),
                currentCart.getTotaleStimato(), "EUR");
    }

    private int parseQuantity() {
        try {
            return Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void configureCells() {
        supplierComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(FornitoreBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });
        supplierComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FornitoreBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });

        supplierProductsListView.setCellFactory(listView -> productCell());
        cartListView.setCellFactory(listView -> productCell());
    }

    private void loadSuppliers() {
        EsitoListaBean<FornitoreBean> esito = boundary.recuperaFornitoriConEsito();
        supplierComboBox.setItems(FXCollections.observableArrayList(esito.getElementi()));
        setMessage(esito.getMessaggio());
    }

    private ListCell<ProdottoBean> productCell() {
        return ProductCardFactory.productCell(false);
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
    }

    private void configureRoleAccess() {
        boolean titolare = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> RuoloUtente.TITOLARE.equals(session.getRuolo()))
                .orElse(false);
        supplierComboBox.setDisable(!titolare);
        supplierProductsListView.setDisable(!titolare);
        loadProductsButton.setDisable(!titolare);
        quantityField.setDisable(!titolare);
        addToCartButton.setDisable(!titolare);
        cartListView.setDisable(!titolare);
        visaRadioButton.setDisable(!titolare);
        paypalRadioButton.setDisable(!titolare);
        cardNumberField.setDisable(!titolare);
        cvvField.setDisable(!titolare);
        paypalEmailField.setDisable(!titolare);
        payAndConfirmButton.setDisable(!titolare);
        if (!titolare) {
            setMessage("Solo il titolare puo acquistare dai fornitori");
        }
    }

    private void updatePaymentFields() {
        boolean visaSelected = visaRadioButton.isSelected();
        cardNumberField.setVisible(visaSelected);
        cardNumberField.setManaged(visaSelected);
        cvvField.setVisible(visaSelected);
        cvvField.setManaged(visaSelected);
        paypalEmailField.setVisible(!visaSelected);
        paypalEmailField.setManaged(!visaSelected);
    }
}
