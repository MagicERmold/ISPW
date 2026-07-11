package com.stocktrack.pattern.adapter;

import com.stocktrack.config.AppConfig;
import com.stocktrack.config.PersistenceMode;
import com.stocktrack.entity.Fornitore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FornitoreApiAdaptee {

    private static final String APPLE_CODE = "APPLE-2026";
    private static final String SAMSUNG_CODE = "SAMSUNG-2026";
    private static final String HUAWEI_CODE = "HUAWEI-2026";
    private static final Path DATA_DIR = resolveDataDir();
    private static final Path SUPPLIER_PRODUCTS_FILE = DATA_DIR.resolve("fornitore_prodotti.csv");
    private static final Object SUPPLIER_PRODUCTS_LOCK = new Object();
    private static final Map<String, Fornitore> SUPPLIER_CATALOG = Map.of(
            APPLE_CODE, new Fornitore(APPLE_CODE, "APPLE", "business@apple.example",
                    "simulated://fornitori/apple", true),
            SAMSUNG_CODE, new Fornitore(SAMSUNG_CODE, "SAMSUNG", "business@samsung.example",
                    "simulated://fornitori/samsung", true),
            HUAWEI_CODE, new Fornitore(HUAWEI_CODE, "HUAWEI", "business@huawei.example",
                    "simulated://fornitori/huawei", true)
    );
    private static final Map<String, List<String>> DEFAULT_SUPPLIER_PRODUCTS = Map.of(
            APPLE_CODE, List.of(
                    "APL-IPHONE15;iPhone 15;Smartphone;40;739.00",
                    "APL-MACBOOKAIR;MacBook Air M3;Notebook;18;1199.00",
                    "APL-AIRPODS;AirPods Pro;Audio;55;219.00"
            ),
            SAMSUNG_CODE, List.of(
                    "SAM-GALAXYS24;Galaxy S24;Smartphone;35;699.00",
                    "SAM-TV55;Samsung TV OLED 55;TV;14;899.00",
                    "SAM-TABS9;Galaxy Tab S9;Tablet;25;579.00"
            ),
            HUAWEI_CODE, List.of(
                    "HUA-PURA70;Huawei Pura 70;Smartphone;22;649.00",
                    "HUA-MATEPAD11;Huawei MatePad 11;Tablet;30;349.00",
                    "HUA-FREEBUDS;Huawei FreeBuds Pro;Audio;45;139.00"
            )
    );
    private static final Map<String, List<String>> DEMO_SUPPLIER_PRODUCTS =
            copySupplierProducts(DEFAULT_SUPPLIER_PRODUCTS);

    public List<String> fetchSupplierProducts(String endpoint, String supplierCode) {
        checkEndpoint(endpoint);
        synchronized (SUPPLIER_PRODUCTS_LOCK) {
            return new ArrayList<>(loadSupplierProductsForCurrentMode().getOrDefault(supplierCode, List.of()));
        }
    }

    public boolean sendOrderNotification(String endpoint, String orderPayload) {
        checkEndpoint(endpoint);
        return orderPayload != null && !orderPayload.isBlank() && !endpoint.contains("failnotify");
    }

    public void saveSupplierProduct(String endpoint, String supplierCode, String rawProduct) {
        checkEndpoint(endpoint);
        String[] newColumns = rawProduct.split(";", -1);
        synchronized (SUPPLIER_PRODUCTS_LOCK) {
            Map<String, List<String>> supplierProducts = loadSupplierProductsForCurrentMode();
            List<String> products = supplierProducts.computeIfAbsent(supplierCode, key -> new ArrayList<>());
            for (int index = 0; index < products.size(); index++) {
                String[] currentColumns = products.get(index).split(";", -1);
                if (currentColumns[0].equals(newColumns[0])) {
                    products.set(index, rawProduct);
                    saveSupplierProductsForCurrentMode(supplierProducts);
                    return;
                }
            }
            products.add(rawProduct);
            saveSupplierProductsForCurrentMode(supplierProducts);
        }
    }

    public void decreaseSupplierProductStock(String supplierCode, String productId, int quantity) {
        synchronized (SUPPLIER_PRODUCTS_LOCK) {
            Map<String, List<String>> supplierProducts = loadSupplierProductsForCurrentMode();
            List<String> products = supplierProducts.get(supplierCode);
            if (products == null) {
                throw new IllegalStateException("Fornitore non censito");
            }

            for (int index = 0; index < products.size(); index++) {
                String[] columns = products.get(index).split(";", -1);
                if (columns[0].equals(productId)) {
                    int availableQuantity = Integer.parseInt(columns[3]);
                    if (availableQuantity < quantity) {
                        throw new IllegalStateException("Rimanenza fornitore insufficiente per " + columns[1]);
                    }
                    columns[3] = Integer.toString(availableQuantity - quantity);
                    products.set(index, String.join(";", columns));
                    saveSupplierProductsForCurrentMode(supplierProducts);
                    return;
                }
            }

            throw new IllegalStateException("Prodotto non trovato nell'inventario fornitore");
        }
    }

    public Optional<Fornitore> findSupplierByCode(String supplierCode) {
        if (supplierCode == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(SUPPLIER_CATALOG.get(supplierCode.trim().toUpperCase()));
    }

    private static Map<String, List<String>> loadSupplierProducts() {
        ensureSeedData();
        try {
            Map<String, List<String>> supplierProducts = new LinkedHashMap<>();
            for (String line : Files.readAllLines(SUPPLIER_PRODUCTS_FILE, StandardCharsets.UTF_8)) {
                String[] columns = line.split(";", -1);
                if (!line.isBlank() && columns.length >= 6) {
                    supplierProducts.computeIfAbsent(columns[0], key -> new ArrayList<>())
                            .add(String.join(";", columns[1], columns[2], columns[3], columns[4], columns[5]));
                }
            }
            return supplierProducts;
        } catch (IOException e) {
            throw new IllegalStateException("Errore lettura inventario fornitori simulato", e);
        }
    }

    private static void saveSupplierProducts(Map<String, List<String>> supplierProducts) {
        List<String> rows = new ArrayList<>();
        supplierProducts.forEach((supplierCode, products) ->
                products.forEach(product -> rows.add(supplierCode + ";" + product)));
        try {
            Files.createDirectories(DATA_DIR);
            Files.write(SUPPLIER_PRODUCTS_FILE, rows, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Errore scrittura inventario fornitori simulato", e);
        }
    }

    private static Map<String, List<String>> loadSupplierProductsForCurrentMode() {
        if (isDemoMode()) {
            return copySupplierProducts(DEMO_SUPPLIER_PRODUCTS);
        }
        return loadSupplierProducts();
    }

    private static void saveSupplierProductsForCurrentMode(Map<String, List<String>> supplierProducts) {
        if (isDemoMode()) {
            DEMO_SUPPLIER_PRODUCTS.clear();
            DEMO_SUPPLIER_PRODUCTS.putAll(copySupplierProducts(supplierProducts));
            return;
        }
        saveSupplierProducts(supplierProducts);
    }

    private static Map<String, List<String>> copySupplierProducts(Map<String, List<String>> source) {
        Map<String, List<String>> copy = new LinkedHashMap<>();
        source.forEach((supplierCode, products) -> copy.put(supplierCode, new ArrayList<>(products)));
        return copy;
    }

    private static boolean isDemoMode() {
        return PersistenceMode.DEMO.equals(new AppConfig().getPersistenceMode());
    }

    private static void ensureSeedData() {
        try {
            Files.createDirectories(DATA_DIR);
            if (Files.exists(SUPPLIER_PRODUCTS_FILE)) {
                return;
            }
            saveSupplierProducts(new LinkedHashMap<>(DEFAULT_SUPPLIER_PRODUCTS));
        } catch (IOException e) {
            throw new IllegalStateException("Errore inizializzazione inventario fornitori simulato", e);
        }
    }

    private static Path resolveDataDir() {
        Path currentDir = Path.of("").toAbsolutePath().normalize();
        Path fileName = currentDir.getFileName();
        if (fileName != null && "StockTrack".equalsIgnoreCase(fileName.toString())) {
            return currentDir.resolve("data");
        }
        return currentDir.resolve("StockTrack").resolve("data");
    }

    private void checkEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank() || endpoint.contains("offline")) {
            throw new IllegalStateException("API fornitore non raggiungibile");
        }
        if (endpoint.contains("timeout")) {
            throw new IllegalStateException("Timeout collegamento API fornitore");
        }
    }
}
