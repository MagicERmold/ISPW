package com.stocktrack.persistence.fs;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileSystemStockDAO implements StockDAO {
    private static final String CSV_FILE_NAME = "stocks.csv";
    private static final Logger logger = Logger.getLogger(FileSystemStockDAO.class.getName());
    private final File file = new File(System.getProperty("stocktrack.fs.stock.file", CSV_FILE_NAME));

    public FileSystemStockDAO() {
        try {
            boolean isCreated = file.createNewFile();
            if (isCreated) {
                logger.info("Nuovo file database creato: " + CSV_FILE_NAME);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Errore: Impossibile creare o accedere al file " + CSV_FILE_NAME, e);
        }
    }

    @Override
    public void saveStock(Stock stock) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(CsvCodec.join(
                    stock.getName(),
                    String.valueOf(stock.getQuantity()),
                    String.valueOf(stock.getThreshold()),
                    stock.getGroupId(),
                    stock.getCategory()));
            writer.newLine();
        } catch (IOException e) {
            throw new StorageException("Errore durante il salvataggio del prodotto", e);
        }
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) throws StorageException {
        try {
            return readAllInternal(groupUid);
        } catch (IOException e) {
            throw new StorageException("Errore lettura database stock", e);
        }
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupId) throws StorageException {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = CsvCodec.split(line);
                if (parts.length >= 4 && parts[0].equalsIgnoreCase(stockName) && parts[3].equals(groupId)) {
                    String category = parts.length > 4 ? parts[4] : "Generico";
                    lines.add(CsvCodec.join(parts[0], String.valueOf(newQuantity), parts[2], parts[3], category));
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura file durante aggiornamento stock", e);
        }

        if (!found) {
            throw new StorageException("Prodotto non trovato nel file system");
        }

        rewriteFile(lines);
    }

    @Override
    public void deleteStock(String stockName, String groupId) throws StorageException {
        List<String> linesToKeep = new ArrayList<>();
        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = CsvCodec.split(line);
                if (parts.length >= 4 && parts[0].equalsIgnoreCase(stockName) && parts[3].equals(groupId)) {
                    removed = true;
                    continue;
                }
                linesToKeep.add(line);
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura file per eliminazione", e);
        }

        if (!removed) {
            throw new StorageException("Prodotto non trovato nel file system");
        }

        rewriteFile(linesToKeep);
    }

    @Override
    public List<String> getAllCategories(String groupId) throws StorageException {
        try {
            return readAllInternal(groupId).stream()
                    .map(Stock::getCategory)
                    .filter(category -> category != null && !category.isBlank())
                    .distinct()
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Errore recupero categorie", e);
        }
    }

    @Override
    public List<Stock> getStocksByCategory(String groupUid, String category) throws StorageException {
        try {
            return readAllInternal(groupUid).stream()
                    .filter(stock -> stock.getCategory() != null && stock.getCategory().equalsIgnoreCase(category))
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Errore recupero prodotti per categoria", e);
        }
    }

    private List<Stock> readAllInternal(String groupUid) throws IOException {
        List<Stock> list = new ArrayList<>();
        if (!file.exists() || groupUid == null) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = CsvCodec.split(line);
                if (parts.length >= 4 && parts[3].equals(groupUid)) {
                    String category = parts.length > 4 ? parts[4] : "Generico";
                    try {
                        list.add(new Stock(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3], category));
                    } catch (NumberFormatException e) {
                        throw new IOException("Riga stock malformata: " + line, e);
                    }
                }
            }
        }
        return list;
    }

    private void rewriteFile(List<String> lines) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Errore scrittura file", e);
        }
    }
}
