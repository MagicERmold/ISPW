package com.stocktrack.persistence.fs;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemStockDAO implements StockDAO {
    private static final String CSV_FILE_NAME = "stock_database.csv";
    private static final Logger logger = Logger.getLogger(FileSystemStockDAO.class.getName());
    private final File file = new File(CSV_FILE_NAME);

    public FileSystemStockDAO() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Impossibile creare il database", e);
            }
        }
    }

    @Override
    public void saveStock(Stock stock) throws StorageException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String line = String.format("%s,%d,%d,%s",
                    stock.getNome(), stock.getQuantity(), stock.getSoglia(), stock.getGroupUid());
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new StorageException("Errore durante il salvataggio del prodotto", e);
        }
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) throws StorageException{
        List<Stock> list = new ArrayList<>();
        if (groupUid == null || groupUid.equals("null")) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // Filtra per gruppo
                    if (parts[3].equals(groupUid)) {
                        list.add(new Stock(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3]));
                    }
                }
            }
        } catch (IOException e) {
            throw new StorageException("Errore durante la lettura del magazzino", e);
        }
        return list;
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException{
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(stockName) && parts[3].equals(groupUid)) {
                    // Aggiorna
                    String newLine = String.format("%s,%d,%s,%s", parts[0], newQuantity, parts[2], parts[3]);
                    lines.add(newLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura file per aggiornamento", e);
        }

        if (found) {
            writeAllLines(lines);
        }
    }

    @Override
    public void deleteStock(String stockName, String groupUid) throws StorageException {
        List<String> linesToKeep = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Se corrisponde a nome E gruppo, lo saltiamo (delete)
                if (parts.length >= 4 && parts[0].equals(stockName) && parts[3].equals(groupUid)) {
                    continue;
                }
                linesToKeep.add(line);
            }
        } catch (IOException e) {
            throw new StorageException("Errore lettura file per eliminazione", e);
        }

        writeAllLines(linesToKeep);
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
}