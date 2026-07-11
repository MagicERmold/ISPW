package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.ProdottoSelezionatoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.QuantitaProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AnalizzaDisponibilitaInventarioBoundary;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import com.stocktrack.boundary.GestisciProdottiBoundary;
import com.stocktrack.common.AbstractProdottoData;
import com.stocktrack.pattern.singleton.Session;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.view.fx.component.ProductCardFactory;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class InventarioFXController {

    @FXML
    private TilePane inventoryTilePane;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button buyFromSuppliersButton;

    @FXML
    private Button suppliersButton;

    private final AnalizzaDisponibilitaInventarioBoundary boundary = new AnalizzaDisponibilitaInventarioBoundary();
    private final AcquistaProdottiFornitoriBoundary loginBoundary = new AcquistaProdottiFornitoriBoundary();
    private final GestisciProdottiBoundary productManagementBoundary = new GestisciProdottiBoundary();

    @FXML
    private void initialize() {
        configureRoleActions();
        loadInventory();
    }

    @FXML
    private void onRefreshInventory() {
        loadInventory();
    }

    @FXML
    private void onBuyFromSuppliers() throws IOException {
        JavaFXApp.setRoot("acquista_prodotti_fornitori");
    }

    @FXML
    private void onManageSuppliers() throws IOException {
        JavaFXApp.setRoot("fornitori");
    }

    @FXML
    private void onLogout() throws IOException {
        loginBoundary.logout();
        JavaFXApp.setRoot("login");
    }

    private void loadInventory() {
        EsitoListaBean<DisponibilitaProdottoBean> esito = boundary.analizzaDisponibilitaConEsito();
        List<DisponibilitaProdottoBean> prodotti = esito.getElementi();
        inventoryTilePane.getChildren().clear();
        boolean puoGestireProdotti = canManageProducts();
        for (DisponibilitaProdottoBean disponibilita : prodotti) {
            inventoryTilePane.getChildren().add(ProductCardFactory.createAvailabilityCard(disponibilita,
                    puoGestireProdotti ? event -> openProductDialog(disponibilita.getProdotto()) : null));
        }
        long belowThreshold = prodotti.stream()
                .map(DisponibilitaProdottoBean::getProdotto)
                .filter(Objects::nonNull)
                .filter(AbstractProdottoData::isSottoSoglia)
                .count();
        summaryLabel.setText("Prodotti: " + prodotti.size() + " | Da riordinare: " + belowThreshold);
        messageLabel.setText(esito.getMessaggio());
    }

    private void configureRoleActions() {
        RuoloUtente ruolo = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(Session::getRuolo)
                .orElse(null);
        boolean titolare = RuoloUtente.TITOLARE.equals(ruolo);
        buyFromSuppliersButton.setVisible(titolare);
        buyFromSuppliersButton.setManaged(titolare);
        suppliersButton.setVisible(titolare);
        suppliersButton.setManaged(titolare);
    }

    private boolean canManageProducts() {
        RuoloUtente ruolo = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(Session::getRuolo)
                .orElse(null);
        return RuoloUtente.TITOLARE.equals(ruolo) || RuoloUtente.COMMESSO.equals(ruolo);
    }

    private void openProductDialog(ProdottoBean prodotto) {
        if (prodotto == null) {
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Gestisci prodotto");
        dialog.setHeaderText(prodotto.getNome());
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE));

        TextField quantityField = new TextField(Integer.toString(prodotto.getQuantita()));
        TextField movementQuantityField = new TextField();
        movementQuantityField.setPromptText("Quantita movimento");
        Label dialogMessageLabel = new Label();
        dialogMessageLabel.getStyleClass().add("message");

        Button saveQuantityButton = new Button("Salva quantita");
        saveQuantityButton.setOnAction(event -> handleProductOperation(dialog, dialogMessageLabel,
                () -> productManagementBoundary.modificaQuantitaProdotto(new QuantitaProdottoBean(prodotto.getId(),
                        parseInt(quantityField.getText()))), true));

        Button registerSaleButton = new Button("Vendita");
        registerSaleButton.setOnAction(event -> handleProductOperation(dialog, dialogMessageLabel,
                () -> productManagementBoundary.registraVenditaManuale(new QuantitaProdottoBean(prodotto.getId(),
                        parseInt(movementQuantityField.getText()))), true));

        Button registerPurchaseButton = new Button("Acquisto");
        registerPurchaseButton.setOnAction(event -> handleProductOperation(dialog, dialogMessageLabel,
                () -> productManagementBoundary.registraAcquistoEsterno(new QuantitaProdottoBean(prodotto.getId(),
                        parseInt(movementQuantityField.getText()))), true));

        Button deleteButton = new Button("Elimina prodotto");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> confirmAndDeleteProduct(dialog, dialogMessageLabel, prodotto));

        GridPane detailsGrid = createProductDetailsGrid(prodotto, quantityField);
        HBox quantityActions = new HBox(8, saveQuantityButton);
        HBox movementActions = new HBox(8, registerSaleButton, registerPurchaseButton);
        VBox content = new VBox(12, detailsGrid, quantityActions,
                new Label("Movimento manuale"), movementQuantityField, movementActions, deleteButton,
                dialogMessageLabel);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private GridPane createProductDetailsGrid(ProdottoBean prodotto, TextField quantityField) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(12);
        gridPane.setVgap(8);
        addDetailRow(gridPane, 0, "Id", prodotto.getId());
        addDetailRow(gridPane, 1, "Nome", prodotto.getNome());
        addDetailRow(gridPane, 2, "Categoria", prodotto.getCategoria());
        addDetailRow(gridPane, 3, "Soglia", Integer.toString(prodotto.getSogliaMinima()));
        addDetailRow(gridPane, 4, "Prezzo", prodotto.getPrezzoUnitario() + " EUR");
        gridPane.add(new Label("Quantita"), 0, 5);
        gridPane.add(quantityField, 1, 5);
        return gridPane;
    }

    private void addDetailRow(GridPane gridPane, int row, String label, String value) {
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("subtitle");
        gridPane.add(nameLabel, 0, row);
        gridPane.add(new Label(value == null || value.isBlank() ? "-" : value), 1, row);
    }

    private void confirmAndDeleteProduct(Dialog<Void> dialog, Label dialogMessageLabel, ProdottoBean prodotto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimina prodotto");
        alert.setHeaderText("Rimuovere " + prodotto.getNome() + " dall'inventario?");
        alert.setContentText("L'operazione elimina il prodotto dal magazzino corrente.");
        alert.initOwner(dialog.getDialogPane().getScene().getWindow());
        alert.showAndWait()
                .filter(ButtonType.OK::equals)
                .ifPresent(buttonType -> handleProductOperation(dialog, dialogMessageLabel,
                        () -> productManagementBoundary.rimuoviProdotto(new ProdottoSelezionatoBean(prodotto.getId())),
                        true));
    }

    private void handleProductOperation(Dialog<Void> dialog, Label dialogMessageLabel, ProductOperation operation,
                                        boolean closeOnSuccess) {
        EsitoOperazioneBean esito = operation.execute();
        dialogMessageLabel.setText(esito.getMessaggio());
        messageLabel.setText(esito.getMessaggio());
        if (esito.isSuccesso()) {
            loadInventory();
            if (closeOnSuccess) {
                dialog.close();
            }
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @FunctionalInterface
    private interface ProductOperation {
        EsitoOperazioneBean execute();
    }
}
