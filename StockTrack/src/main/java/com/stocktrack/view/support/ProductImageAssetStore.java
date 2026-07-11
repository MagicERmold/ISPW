package com.stocktrack.view.support;

import com.stocktrack.bean.ProdottoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProductImageAssetStore {

    private ProductImageAssetStore() {
    }

    public static void saveProductImage(Path sourceImage, ProdottoBean prodottoBean) throws IOException {
        String extension = extensionOf(sourceImage);
        if (extension.isBlank()) {
            throw new IOException("Formato foto non supportato");
        }

        for (Path imagesDirectory : imageDirectories()) {
            if (Files.exists(imagesDirectory.getParent())) {
                Files.createDirectories(imagesDirectory);
                for (String stem : imageStemsFor(prodottoBean)) {
                    Files.copy(sourceImage, imagesDirectory.resolve(stem + extension),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static List<String> imageStemsFor(ProdottoBean prodotto) {
        ProdottoBean safeProduct = prodotto == null ? new ProdottoBean() : prodotto;
        String id = safeText(safeProduct.getId());
        String name = safeText(safeProduct.getNome());
        List<String> stems = new ArrayList<>();
        addStem(stems, slug(name));
        addStem(stems, slug(id));

        String lowerName = name.toLowerCase(Locale.ROOT);
        if (lowerName.contains("galaxy") || id.toUpperCase(Locale.ROOT).contains("SAM")) {
            addStem(stems, "samsung_" + slug(name));
        }
        if (lowerName.contains("iphone") || lowerName.contains("airpods")
                || id.toUpperCase(Locale.ROOT).contains("APL")) {
            addStem(stems, "apple_" + slug(name));
        }
        if (lowerName.contains("huawei") || id.toUpperCase(Locale.ROOT).contains("HUA")) {
            addStem(stems, "huawei_" + slug(name.replaceFirst("(?i)^huawei\\s+", "")));
        }
        return stems;
    }

    private static List<Path> imageDirectories() {
        Path projectRoot = Files.exists(Path.of("src", "main", "resources"))
                ? Path.of("")
                : Path.of("StockTrack");
        return List.of(projectRoot.resolve(Path.of("src", "main", "resources", "Images")).normalize(),
                projectRoot.resolve(Path.of("target", "classes", "Images")).normalize());
    }

    private static String extensionOf(Path sourceImage) {
        String fileName = sourceImage == null || sourceImage.getFileName() == null
                ? ""
                : sourceImage.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        String extension = fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
        return switch (extension) {
            case ".png", ".jpg", ".jpeg" -> extension;
            default -> "";
        };
    }

    private static void addStem(List<String> stems, String stem) {
        if (!stem.isBlank() && !stems.contains(stem)) {
            stems.add(stem);
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_");
        return trimBoundaryUnderscores(slug);
    }

    private static String trimBoundaryUnderscores(String value) {
        int start = 0;
        int end = value.length();
        while (start < end && value.charAt(start) == '_') {
            start++;
        }
        while (end > start && value.charAt(end - 1) == '_') {
            end--;
        }
        return value.substring(start, end);
    }

    private static String safeText(String value) {
        return value == null || value.isBlank() ? "" : value;
    }
}
