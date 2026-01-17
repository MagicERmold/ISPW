package com.stocktrack.persistence.fs;

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
    private final File file;

    public FileSystemStockDAO() {
        this.file = new File(CSV_FILE_NAME);
        // Se il file non esiste, proviamo a crearlo
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    logger.info("File database stock creato: " + CSV_FILE_NAME);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Impossibile creare il file database stock", e);
            }
        }
    }

    @Override
    public void saveStock(Stock stock) {
        // 'true' abilita la modalità append (scrive in coda senza sovrascrivere)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            // Scriviamo: Nome,Quantità,Soglia
            // Assumiamo che la tua classe Stock abbia i metodi getNome(), getQuantity(), getSoglia()
            // Se getQuantity() si chiama getQuantita(), correggi qui sotto.
            String line = String.format("%s,%d,%d",
                    stock.getNome(),
                    stock.getQuantity(),
                    stock.getSoglia());

            writer.write(line);
            writer.newLine(); // A capo

            logger.info(() -> "Prodotto salvato su file: " + stock.getNome());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio su file", e);
        }
    }

    @Override
    public List<Stock> getAllStocks() {
        List<Stock> stockList = new ArrayList<>();

        if (!file.exists()) return stockList;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                // Ci aspettiamo 3 campi: Nome, Quantità, Soglia
                if (parts.length >= 3) {
                    String name = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    int threshold = Integer.parseInt(parts[2]);

                    // Creiamo l'oggetto usando il costruttore completo
                    Stock s = new Stock(name, quantity, threshold);
                    stockList.add(s);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del database stock", e);
        }

        return stockList;
    }
}