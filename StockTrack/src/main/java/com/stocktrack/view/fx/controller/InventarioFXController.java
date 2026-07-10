package com.stocktrack.view.fx.controller;

import com.stocktrack.JavaFXApp;
import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.boundary.AnalizzaDisponibilitaInventarioBoundary;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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

    private final AnalizzaDisponibilitaInventarioBoundary boundary = new AnalizzaDisponibilitaInventarioBoundary();

    @FXML
    private void initialize() {
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

    private void loadInventory() {
        List<DisponibilitaProdottoBean> prodotti = boundary.analizzaDisponibilita();
        inventoryTilePane.getChildren().clear();
        for (DisponibilitaProdottoBean disponibilita : prodotti) {
            inventoryTilePane.getChildren().add(createProductCard(disponibilita));
        }
        long belowThreshold = prodotti.stream()
                .map(DisponibilitaProdottoBean::getProdotto)
                .filter(Objects::nonNull)
                .filter(prodotto -> prodotto.isSottoSoglia())
                .count();
        summaryLabel.setText("Prodotti: " + prodotti.size() + " | Da riordinare: " + belowThreshold);
        messageLabel.setText(prodotti.isEmpty() ? "Inventario vuoto" : "Inventario aggiornato");
    }

    private VBox createProductCard(DisponibilitaProdottoBean disponibilita) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");

        String productName = disponibilita.getProdotto().getNome();
        ImageView imageView = new ImageView(createProductImage(productName, disponibilita.getProdotto().getCategoria()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(96);

        Label nameLabel = new Label(productName);
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        Label quantityLabel = new Label("Disponibili: " + disponibilita.getQuantitaDisponibile());
        quantityLabel.getStyleClass().add(disponibilita.isDisponibile() ? "availability-ok" : "availability-ko");

        Label statusLabel = new Label(disponibilita.getMessaggio());
        statusLabel.getStyleClass().add("subtitle");

        card.getChildren().addAll(imageView, nameLabel, quantityLabel, statusLabel);
        return card;
    }

    private Image createProductImage(String productName, String category) {
        int width = 300;
        int height = 190;
        Color color = colorFor(category);
        Rectangle background = new Rectangle(width, height, color);
        background.setArcWidth(20);
        background.setArcHeight(20);

        Text initials = new Text(initials(productName));
        initials.setFill(Color.WHITE);
        initials.setFont(Font.font("Segoe UI", FontWeight.BOLD, 56));

        StackPane stackPane = new StackPane(background, initials);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = new WritableImage(width, height);
        stackPane.snapshot(parameters, image);
        return image;
    }

    private Color colorFor(String category) {
        if (category == null) {
            return Color.web("#22577a");
        }
        return switch (Math.abs(category.hashCode()) % 5) {
            case 0 -> Color.web("#22577a");
            case 1 -> Color.web("#2a9d8f");
            case 2 -> Color.web("#6a4c93");
            case 3 -> Color.web("#b56576");
            default -> Color.web("#3d5a80");
        };
    }

    private String initials(String productName) {
        if (productName == null || productName.isBlank()) {
            return "ST";
        }
        String[] parts = productName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}
