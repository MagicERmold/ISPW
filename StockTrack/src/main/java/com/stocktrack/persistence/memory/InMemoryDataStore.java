package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.Titolare;
import com.stocktrack.security.PasswordHasher;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

final class InMemoryDataStore {

    static final Map<String, Titolare> TITOLARI = new LinkedHashMap<>();
    static final Map<String, Commesso> COMMESSI = new LinkedHashMap<>();
    static final Map<String, Fornitore> FORNITORI = new LinkedHashMap<>();
    static final Map<String, Prodotto> PRODOTTI = new LinkedHashMap<>();
    static final Map<String, Ordine> ORDINI = new LinkedHashMap<>();
    private static final String DEFAULT_LOGIN_PASSWORD = "password123";
    static Inventario inventario;

    static {
        Titolare titolare = new Titolare("TIT-1", "Andrea", "Titolare", "titolare@stocktrack.local",
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Commesso commesso = new Commesso("COM-1", "Mario", "Commesso", "commesso@stocktrack.local",
                PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD));
        Fornitore fornitore = new Fornitore("FOR-1", "Forniture Demo", "fornitore@demo.local",
                "simulated://fornitori/demo", true);
        Prodotto caffe = new Prodotto("PROD-1", "Caffe", "Alimentari", 8, 10, new BigDecimal("3.50"));
        Prodotto latte = new Prodotto("PROD-2", "Latte", "Alimentari", 20, 5, new BigDecimal("1.40"));

        TITOLARI.put(titolare.getId(), titolare);
        COMMESSI.put(commesso.getId(), commesso);
        FORNITORI.put(fornitore.getId(), fornitore);
        PRODOTTI.put(caffe.getId(), caffe);
        PRODOTTI.put(latte.getId(), latte);
        inventario = new Inventario("INV-1", PRODOTTI.values().stream().toList());
    }

    private InMemoryDataStore() {
    }
}
