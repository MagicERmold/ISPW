package com.stocktrack.view.fx.component;

import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.view.support.ProductImageAssetStore;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProductCardFactory {

    private static final int CARD_IMAGE_WIDTH = 360;
    private static final int CARD_IMAGE_HEIGHT = 240;
    private static final int FULL_IMAGE_WIDTH = 190;
    private static final int FULL_IMAGE_HEIGHT = 132;
    private static final int COMPACT_IMAGE_WIDTH = 146;
    private static final int COMPACT_IMAGE_HEIGHT = 104;
    private static final String DEFAULT_PRODUCT_COLOR = "#22577a";
    private static final String[] PRODUCT_COLOR_PALETTE = {
            DEFAULT_PRODUCT_COLOR,
            "#2a9d8f",
            "#6a4c93",
            "#b56576",
            "#3d5a80"
    };

    private ProductCardFactory() {
    }

    public static VBox createAvailabilityCard(DisponibilitaProdottoBean disponibilita) {
        return createAvailabilityCard(disponibilita, null);
    }

    public static VBox createAvailabilityCard(DisponibilitaProdottoBean disponibilita,
                                              EventHandler<ActionEvent> editHandler) {
        ProdottoBean prodotto = disponibilita.getProdotto();
        VBox card = createProductCard(prodotto, false, false);

        Label quantityLabel = new Label("Disponibili: " + disponibilita.getQuantitaDisponibile());
        quantityLabel.getStyleClass().add(disponibilita.isDisponibile() ? "availability-ok" : "availability-ko");

        Label statusLabel = new Label(disponibilita.getMessaggio());
        statusLabel.getStyleClass().add("subtitle");
        statusLabel.setWrapText(true);

        card.getChildren().addAll(quantityLabel, statusLabel);
        if (editHandler != null) {
            Button editButton = new Button("Modifica");
            editButton.setMaxWidth(Double.MAX_VALUE);
            editButton.setOnAction(editHandler);
            card.getChildren().add(editButton);
        }
        return card;
    }

    public static VBox createProductCard(ProdottoBean prodotto, boolean showThreshold, boolean compact) {
        ProdottoBean safeProduct = prodotto == null ? new ProdottoBean() : prodotto;
        VBox card = new VBox(compact ? 6 : 8);
        card.getStyleClass().add(compact ? "product-card-compact" : "product-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);

        ImageView imageView = new ImageView(resolveProductImage(safeProduct));
        imageView.getStyleClass().add("product-image");
        imageView.setFitWidth(compact ? COMPACT_IMAGE_WIDTH : FULL_IMAGE_WIDTH);
        imageView.setFitHeight(compact ? COMPACT_IMAGE_HEIGHT : FULL_IMAGE_HEIGHT);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane imageFrame = new StackPane(imageView);
        imageFrame.getStyleClass().add("product-image-frame");
        imageFrame.setMinSize(compact ? COMPACT_IMAGE_WIDTH : FULL_IMAGE_WIDTH,
                compact ? COMPACT_IMAGE_HEIGHT : FULL_IMAGE_HEIGHT);
        imageFrame.setPrefSize(compact ? COMPACT_IMAGE_WIDTH : FULL_IMAGE_WIDTH,
                compact ? COMPACT_IMAGE_HEIGHT : FULL_IMAGE_HEIGHT);
        imageFrame.setMaxSize(compact ? COMPACT_IMAGE_WIDTH : FULL_IMAGE_WIDTH,
                compact ? COMPACT_IMAGE_HEIGHT : FULL_IMAGE_HEIGHT);

        Label nameLabel = new Label(safeText(safeProduct.getNome(), "Prodotto"));
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        Label quantityLabel = new Label("Qta: " + safeProduct.getQuantita());
        quantityLabel.getStyleClass().add("subtitle");

        BigDecimal prezzo = safeProduct.getPrezzoUnitario() == null ? BigDecimal.ZERO : safeProduct.getPrezzoUnitario();
        Label priceLabel = new Label(prezzo + " EUR");
        priceLabel.getStyleClass().add("total");

        card.getChildren().addAll(imageFrame, nameLabel, quantityLabel, priceLabel);
        if (showThreshold) {
            Label thresholdLabel = new Label("Soglia: " + safeProduct.getSogliaMinima());
            thresholdLabel.getStyleClass().add(safeProduct.isSottoSoglia() ? "availability-ko" : "availability-ok");
            card.getChildren().add(thresholdLabel);
        }
        return card;
    }

    public static ListCell<ProdottoBean> productCell(boolean showThreshold) {
        return new ListCell<>() {
            @Override
            protected void updateItem(ProdottoBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(createProductCard(item, showThreshold, true));
            }
        };
    }

    private static Image resolveProductImage(ProdottoBean prodotto) {
        List<String> candidates = imageCandidates(prodotto);
        for (String candidate : candidates) {
            URL resource = ProductCardFactory.class.getResource(candidate);
            if (resource != null) {
                return new Image(resource.toExternalForm(), CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, true, true);
            }
        }
        return createPlaceholderImage(prodotto);
    }

    private static List<String> imageCandidates(ProdottoBean prodotto) {
        List<String> candidates = new ArrayList<>();
        for (String stem : ProductImageAssetStore.imageStemsFor(prodotto)) {
            candidates.add("/Images/" + stem + ".png");
            candidates.add("/Images/" + stem + ".jpg");
            candidates.add("/Images/" + stem + ".jpeg");
        }
        return candidates;
    }

    private static Image createPlaceholderImage(ProdottoBean prodotto) {
        int width = CARD_IMAGE_WIDTH;
        int height = CARD_IMAGE_HEIGHT;
        Color color = colorFor(prodotto.getCategoria());
        Rectangle background = new Rectangle(width, height, color);
        background.setArcWidth(20);
        background.setArcHeight(20);

        Text initials = new Text(initials(prodotto.getNome()));
        initials.setFill(Color.WHITE);
        initials.setFont(Font.font("Segoe UI", FontWeight.BOLD, 56));

        StackPane stackPane = new StackPane(background, initials);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = new WritableImage(width, height);
        stackPane.snapshot(parameters, image);
        return image;
    }

    private static Color colorFor(String category) {
        if (category == null) {
            return Color.web(DEFAULT_PRODUCT_COLOR);
        }
        int colorIndex = Math.floorMod(category.hashCode(), PRODUCT_COLOR_PALETTE.length);
        return Color.web(PRODUCT_COLOR_PALETTE[colorIndex]);
    }

    private static String initials(String productName) {
        if (productName == null || productName.isBlank()) {
            return "ST";
        }
        String[] parts = productName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private static String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
