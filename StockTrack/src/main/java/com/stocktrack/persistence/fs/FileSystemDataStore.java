package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.security.PasswordHasher;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class FileSystemDataStore {

    private static final Path DATA_DIR = Path.of("StockTrack", "data");
    private static final Path TITOLARI_FILE = DATA_DIR.resolve("titolari.csv");
    private static final Path COMMESSI_FILE = DATA_DIR.resolve("commessi.csv");
    private static final Path FORNITORI_FILE = DATA_DIR.resolve("fornitori.csv");
    private static final Path PRODOTTI_FILE = DATA_DIR.resolve("prodotti.csv");
    private static final Path ORDINI_FILE = DATA_DIR.resolve("ordini.csv");
    private static final String DEFAULT_LOGIN_PASSWORD = "password123";

    private FileSystemDataStore() {
    }

    static Optional<Titolare> findTitolareById(String id) throws PersistenceException {
        return loadTitolari().stream().filter(titolare -> id.equals(titolare.getId())).findFirst();
    }

    static Optional<Titolare> findTitolareByEmail(String email) throws PersistenceException {
        return loadTitolari().stream()
                .filter(titolare -> titolare.getEmail() != null && titolare.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    static List<Titolare> loadTitolari() throws PersistenceException {
        ensureSeedData();
        return readRows(TITOLARI_FILE).stream()
                .map(row -> new Titolare(row[0], row[1], row[2], row[3], credentialAt(row)))
                .toList();
    }

    static void saveTitolare(Titolare titolare) throws PersistenceException {
        Map<String, Titolare> titolari = new LinkedHashMap<>();
        for (Titolare current : loadTitolari()) {
            titolari.put(current.getId(), current);
        }
        titolari.put(titolare.getId(), titolare);
        writeRows(TITOLARI_FILE, titolari.values().stream()
                .map(current -> List.of(current.getId(), current.getNome(), current.getCognome(),
                        current.getEmail(), safe(current.getPasswordHash())))
                .toList());
    }

    static Optional<Commesso> findCommessoById(String id) throws PersistenceException {
        return loadCommessi().stream().filter(commesso -> id.equals(commesso.getId())).findFirst();
    }

    static Optional<Commesso> findCommessoByEmail(String email) throws PersistenceException {
        return loadCommessi().stream()
                .filter(commesso -> commesso.getEmail() != null && commesso.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    static List<Commesso> loadCommessi() throws PersistenceException {
        ensureSeedData();
        return readRows(COMMESSI_FILE).stream()
                .map(row -> new Commesso(row[0], row[1], row[2], row[3], credentialAt(row)))
                .toList();
    }

    static void saveCommesso(Commesso commesso) throws PersistenceException {
        Map<String, Commesso> commessi = new LinkedHashMap<>();
        for (Commesso current : loadCommessi()) {
            commessi.put(current.getId(), current);
        }
        commessi.put(commesso.getId(), commesso);
        writeRows(COMMESSI_FILE, commessi.values().stream()
                .map(current -> List.of(current.getId(), current.getNome(), current.getCognome(),
                        current.getEmail(), safe(current.getPasswordHash())))
                .toList());
    }

    static List<Fornitore> loadFornitori() throws PersistenceException {
        ensureSeedData();
        return readRows(FORNITORI_FILE).stream()
                .map(row -> new Fornitore(row[0], row[1], row[2], row[3], Boolean.parseBoolean(row[4])))
                .toList();
    }

    static Optional<Fornitore> findFornitoreById(String id) throws PersistenceException {
        return loadFornitori().stream().filter(fornitore -> id.equals(fornitore.getId())).findFirst();
    }

    static void saveFornitore(Fornitore fornitore) throws PersistenceException {
        Map<String, Fornitore> fornitori = new LinkedHashMap<>();
        for (Fornitore current : loadFornitori()) {
            fornitori.put(current.getId(), current);
        }
        fornitori.put(fornitore.getId(), fornitore);
        writeRows(FORNITORI_FILE, fornitori.values().stream()
                .map(current -> List.of(current.getId(), current.getNome(), current.getEmail(),
                        current.getApiEndpoint(), Boolean.toString(current.isDisponibile())))
                .toList());
    }

    static List<Prodotto> loadProdotti() throws PersistenceException {
        ensureSeedData();
        return readRows(PRODOTTI_FILE).stream()
                .map(row -> new Prodotto(row[0], row[1], row[2], Integer.parseInt(row[3]),
                        Integer.parseInt(row[4]), new BigDecimal(row[5])))
                .toList();
    }

    static Optional<Prodotto> findProdottoById(String id) throws PersistenceException {
        return loadProdotti().stream().filter(prodotto -> id.equals(prodotto.getId())).findFirst();
    }

    static void saveProdotto(Prodotto prodotto) throws PersistenceException {
        Map<String, Prodotto> prodotti = new LinkedHashMap<>();
        for (Prodotto current : loadProdotti()) {
            prodotti.put(current.getId(), current);
        }
        prodotti.put(prodotto.getId(), prodotto);
        writeProdotti(new ArrayList<>(prodotti.values()));
    }

    static void deleteProdotto(String id) throws PersistenceException {
        List<Prodotto> prodotti = loadProdotti().stream()
                .filter(prodotto -> !id.equals(prodotto.getId()))
                .toList();
        writeProdotti(prodotti);
    }

    static Inventario loadInventario() throws PersistenceException {
        return new Inventario("INV-FS", loadProdotti());
    }

    static void saveInventario(Inventario inventario) throws PersistenceException {
        writeProdotti(inventario.getProdotti());
    }

    static List<Ordine> loadOrdini() throws PersistenceException {
        ensureSeedData();
        return readRows(ORDINI_FILE).stream()
                .map(row -> {
                    Ordine ordine = new Ordine();
                    ordine.setId(row[0]);
                    ordine.setTotale(new BigDecimal(row[1]));
                    ordine.setStato(row[2]);
                    return ordine;
                })
                .toList();
    }

    static Optional<Ordine> findOrdineById(String id) throws PersistenceException {
        return loadOrdini().stream().filter(ordine -> id.equals(ordine.getId())).findFirst();
    }

    static void saveOrdine(Ordine ordine) throws PersistenceException {
        Map<String, Ordine> ordini = new LinkedHashMap<>();
        for (Ordine current : loadOrdini()) {
            ordini.put(current.getId(), current);
        }
        ordini.put(ordine.getId(), ordine);
        writeRows(ORDINI_FILE, ordini.values().stream()
                .map(current -> List.of(current.getId(), current.getTotale().toPlainString(), current.getStato()))
                .toList());
    }

    private static void writeProdotti(List<Prodotto> prodotti) throws PersistenceException {
        writeRows(PRODOTTI_FILE, prodotti.stream()
                .map(prodotto -> List.of(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                        Integer.toString(prodotto.getQuantita()), Integer.toString(prodotto.getSogliaMinima()),
                        prodotto.getPrezzoUnitario().toPlainString()))
                .toList());
    }

    private static void ensureSeedData() throws PersistenceException {
        try {
            Files.createDirectories(DATA_DIR);
            writeIfMissing(TITOLARI_FILE, List.of("TIT-1;Andrea;Titolare;titolare@stocktrack.local;"
                    + PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD)));
            writeIfMissing(COMMESSI_FILE, List.of("COM-1;Mario;Commesso;commesso@stocktrack.local;"
                    + PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD)));
            writeIfMissing(FORNITORI_FILE, List.of(
                    "FOR-1;Forniture Demo;fornitore@demo.local;simulated://fornitori/demo;true"));
            writeIfMissing(PRODOTTI_FILE, List.of(
                    "PROD-1;Caffe;Alimentari;8;10;3.50",
                    "PROD-2;Latte;Alimentari;20;5;1.40"));
            writeIfMissing(ORDINI_FILE, List.of());
        } catch (IOException e) {
            throw new PersistenceException("Errore inizializzazione persistenza file system", e);
        }
    }

    private static void writeIfMissing(Path path, List<String> rows) throws IOException {
        if (Files.notExists(path)) {
            Files.write(path, rows, StandardCharsets.UTF_8);
        }
    }

    private static List<String[]> readRows(Path path) throws PersistenceException {
        try {
            if (Files.notExists(path)) {
                return List.of();
            }
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split(";", -1))
                    .toList();
        } catch (IOException e) {
            throw new PersistenceException("Errore lettura file " + path, e);
        }
    }

    private static void writeRows(Path path, List<List<String>> rows) throws PersistenceException {
        List<String> lines = rows.stream()
                .map(row -> String.join(";", row))
                .toList();
        try {
            Files.createDirectories(DATA_DIR);
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenceException("Errore scrittura file " + path, e);
        }
    }

    private static String valueAt(String[] row, int index) {
        return row.length > index ? row[index] : "";
    }

    private static String credentialAt(String[] row) {
        String value = valueAt(row, 4);
        if (!value.isBlank()) {
            return value;
        }
        return PasswordHasher.hash(DEFAULT_LOGIN_PASSWORD);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
