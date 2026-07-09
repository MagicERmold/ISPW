package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AcquistaProdottiFornitoriFXController {

    @FXML
    private ComboBox<FornitoreBean> supplierComboBox;

    @FXML
    private ListView<ProdottoBean> supplierProductsListView;

    @FXML
    private TextField quantityField;

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
        supplierComboBox.setItems(FXCollections.observableArrayList(boundary.recuperaFornitori()));
    }

    @FXML
    private void onLoadProducts() {
        FornitoreBean fornitoreBean = supplierComboBox.getValue();
        if (fornitoreBean == null) {
            setMessage("Selezionare un fornitore");
            return;
        }

        List<ProdottoBean> prodotti = boundary.recuperaProdotti(fornitoreBean);
        supplierProductsListView.setItems(FXCollections.observableArrayList(prodotti));
        setMessage(prodotti.isEmpty() ? "Nessun prodotto disponibile" : "Prodotti caricati");
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
        cartListView.setItems(FXCollections.observableArrayList(currentCart.getProdotti()));
        totalLabel.setText("Totale: " + currentCart.getTotaleStimato() + " EUR");
        setMessage("Prodotto aggiunto al carrello");
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

    private ListCell<ProdottoBean> productCell() {
        return new ListCell<>() {
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
        };
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
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
