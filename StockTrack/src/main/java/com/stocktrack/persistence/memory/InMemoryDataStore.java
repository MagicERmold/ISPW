package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.Titolare;
import com.stocktrack.security.PasswordHasher;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Componente interno di persistenza che centralizza strutture e operazioni sulla memoria della modalità DEMO. È usato dai DAO dello stesso package e resta nascosto a boundary, view e controller applicativi.
 */
final class InMemoryDataStore {

    static final Map<String, Titolare> TITOLARI = new ConcurrentHashMap<>();
    static final Map<String, Commesso> COMMESSI = new ConcurrentHashMap<>();
    static final Map<String, Fornitore> FORNITORI = new ConcurrentHashMap<>();
    static final Map<String, Prodotto> PRODOTTI = new ConcurrentHashMap<>();
    static final Map<String, Ordine> ORDINI = new ConcurrentHashMap<>();
    static final Map<String, MovimentoInventario> MOVIMENTI_INVENTARIO = new ConcurrentHashMap<>();
    private static final String DEFAULT_LOGIN_PASSWORD = "password123";
    private static final String APPLE_CODE = "APPLE-2026";
    private static final String SAMSUNG_CODE = "SAMSUNG-2026";
    private static final String HUAWEI_CODE = "HUAWEI-2026";
    private static final String SMARTPHONE_CATEGORY = "Smartphone";
    private static final AtomicReference<Inventario> INVENTARIO = new AtomicReference<>();

    static {
        Titolare titolare = new Titolare("TIT-1", "Andrea", "Titolare", "titolare@euronics.local",
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Commesso commesso = new Commesso("COM-1", "Mario", "Commesso", "commesso@euronics.local",
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Fornitore apple = new Fornitore(APPLE_CODE, "APPLE", "business@apple.example",
                "simulated://fornitori/apple", true, PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Fornitore samsung = new Fornitore(SAMSUNG_CODE, "SAMSUNG", "business@samsung.example",
                "simulated://fornitori/samsung", true, PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Fornitore huawei = new Fornitore(HUAWEI_CODE, "HUAWEI", "business@huawei.example",
                "simulated://fornitori/huawei", true, PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));

        TITOLARI.put(titolare.getId(), titolare);
        COMMESSI.put(commesso.getId(), commesso);
        FORNITORI.put(apple.getId(), apple);
        FORNITORI.put(samsung.getId(), samsung);
        FORNITORI.put(huawei.getId(), huawei);
        addProdotto(new Prodotto("EUR-IPHONE15", "iPhone 15", SMARTPHONE_CATEGORY, 12, 3, new BigDecimal("799.00")));
        addProdotto(new Prodotto("EUR-MACBOOKAIR", "MacBook Air M3", "Notebook", 4, 2, new BigDecimal("1299.00")));
        addProdotto(new Prodotto("EUR-GALAXYS24", "Galaxy S24", SMARTPHONE_CATEGORY, 10, 3, new BigDecimal("749.00")));
        addProdotto(new Prodotto("EUR-SAMSUNGTV55", "Samsung TV OLED 55", "TV", 6, 2, new BigDecimal("999.00")));
        addProdotto(new Prodotto("EUR-HUAWEIPURA70", "Huawei Pura 70", SMARTPHONE_CATEGORY, 5, 2,
                new BigDecimal("699.00")));
        addProdotto(new Prodotto("EUR-HUAWEIMATEPAD", "Huawei MatePad 11", "Tablet", 8, 3,
                new BigDecimal("399.00")));
        INVENTARIO.set(new Inventario("INV-1", PRODOTTI.values().stream().toList()));
    }

    private InMemoryDataStore() {
    }

    static Inventario getInventario() {
        return INVENTARIO.get();
    }

    static void setInventario(Inventario nuovoInventario) {
        INVENTARIO.set(nuovoInventario);
    }

    private static void addProdotto(Prodotto prodotto) {
        PRODOTTI.put(prodotto.getId(), prodotto);
    }
}
