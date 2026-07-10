package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AnalizzaDisponibilitaInventarioBoundary;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import com.stocktrack.common.AbstractProdottoData;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.view.fx.component.ProductCardFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

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

    @FXML
    private Button manageProductsButton;

    private final AnalizzaDisponibilitaInventarioBoundary boundary = new AnalizzaDisponibilitaInventarioBoundary();
    private final AcquistaProdottiFornitoriBoundary loginBoundary = new AcquistaProdottiFornitoriBoundary();

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
    private void onManageProducts() throws IOException {
        JavaFXApp.setRoot("gestisci_prodotti");
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
        List<DisponibilitaProdottoBean> prodotti = boundary.analizzaDisponibilita();
        inventoryTilePane.getChildren().clear();
        for (DisponibilitaProdottoBean disponibilita : prodotti) {
            inventoryTilePane.getChildren().add(ProductCardFactory.createAvailabilityCard(disponibilita));
        }
        long belowThreshold = prodotti.stream()
                .map(DisponibilitaProdottoBean::getProdotto)
                .filter(Objects::nonNull)
                .filter(AbstractProdottoData::isSottoSoglia)
                .count();
        summaryLabel.setText("Prodotti: " + prodotti.size() + " | Da riordinare: " + belowThreshold);
        messageLabel.setText(prodotti.isEmpty() ? "Inventario vuoto" : "Inventario aggiornato");
    }

    private void configureRoleActions() {
        RuoloUtente ruolo = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> session.getRuolo())
                .orElse(null);
        boolean titolare = RuoloUtente.TITOLARE.equals(ruolo);
        boolean puoGestireProdotti = titolare || RuoloUtente.COMMESSO.equals(ruolo);
        buyFromSuppliersButton.setVisible(titolare);
        buyFromSuppliersButton.setManaged(titolare);
        suppliersButton.setVisible(titolare);
        suppliersButton.setManaged(titolare);
        manageProductsButton.setVisible(puoGestireProdotti);
        manageProductsButton.setManaged(puoGestireProdotti);
    }
}
