package com.stocktrack.persistence.fs;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileSystemStockDAO implements StockDAO {
    private static final String CSV_FILE_NAME = "stocks.csv";
    private static final Logger logger = Logger.getLogger(FileSystemStockDAO.class.getName());
    private final File file = new File(CSV_FILE_NAME);

    public FileSystemStockDAO() {
        try {
            boolean isCreated = file.createNewFile();

            if (isCreated) {
                logger.info("Nuovo file database creato: " + CSV_FILE_NAME);
            } else {
                logger.info("File database già esistente: " + CSV_FILE_NAME);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Errore: Impossibile creare o accedere al file " + CSV_FILE_NAME, e);
        }
    }

    @Override
    public void saveStock(Stock stock) throws StorageException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String line = String.format("%s,%d,%d,%s,%s",
                    stock.getName(), stock.getQuantity(), stock.getThreshold(), stock.getGroupId(), stock.getCategory());
            writer.write(line);
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
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(stockName) && parts[3].equals(groupId)) {
                    String cat = (parts.length > 4) ? parts[4] : "Generico";
                    // Riscriviamo mantenendo la categoria esistente
                    String newLine = String.format("%s,%d,%s,%s,%s", parts[0], newQuantity, parts[2], parts[3], cat);
                    lines.add(newLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

        if (found) {
            try {
                rewriteFile(lines);
            } catch (IOException e) {
                throw new StorageException("Errore scrittura file durante aggiornamento stock", e);
            }
        }
    }

    @Override
    public void deleteStock(String stockName, String groupId) throws StorageException {
        List<String> linesToKeep = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Se corrisponde a nome E gruppo, lo saltiamo (delete)
                if (parts.length >= 4 && parts[0].equals(stockName) && parts[3].equals(groupId)) {
                    continue;
                }
                linesToKeep.add(line);
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura file per eliminazione", e);
        }

        writeAllLines(linesToKeep);
    }

    @Override
    public List<String> getAllCategories(String groupId) throws StorageException {
        try {
            return readAllInternal(groupId).stream()
                    .map(Stock::getCategory)
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
                    .filter(s -> s.getCategory().equalsIgnoreCase(category))
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Errore recupero prodotti per categoria", e);
        }
    }

    // Metodo helper privato per evitare duplicazione codice scrittura
    private void writeAllLines(List<String> lines) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String s : lines) {
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Errore scrittura file", e);
        }
    }

    // Helper per leggere tutto e convertire
    private List<Stock> readAllInternal(String groupUid) throws IOException {
        List<Stock> list = new ArrayList<>();
        if (!file.exists() || groupUid == null) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[3].equals(groupUid)) {
                    String cat = (parts.length > 4) ? parts[4] : "Generico"; // Compatibilità vecchi file
                    list.add(new Stock(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3], cat));
                }
            }
        } catch (IOException e) {
            throw new IOException("Errore nella scrittura del prodotto", e);
        }
        return list;
    }

    private void rewriteFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String s : lines) {
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Errore nella scrittura del file", e);
        }
    }
}