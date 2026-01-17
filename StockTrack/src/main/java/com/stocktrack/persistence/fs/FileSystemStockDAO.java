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
    private final File file = new File(CSV_FILE_NAME);

    public FileSystemStockDAO() {
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void saveStock(Stock stock) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String line = String.format("%s,%d,%d,%s",
                    stock.getNome(), stock.getQuantity(), stock.getThreshold(), stock.getGroupUid());
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { logger.log(Level.SEVERE, "Errore save", e); }
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) {
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
        } catch (Exception e) { logger.log(Level.SEVERE, "Errore read", e); }
        return list;
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(stockName) && parts[3].equals(groupUid)) {
                    // Trovato! Aggiorno la quantità (indice 1)
                    String newLine = String.format("%s,%d,%s,%s", parts[0], newQuantity, parts[2], parts[3]);
                    lines.add(newLine);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) { return; }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for (String s : lines) {
                    writer.write(s);
                    writer.newLine();
                }
            } catch (IOException e) { logger.log(Level.SEVERE, "Errore update stock", e); }
        }
    }
}