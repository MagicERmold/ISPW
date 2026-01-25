package com.stocktrack.persistence.serial;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SerializableStockDAO implements StockDAO {
    private static final String FILE_NAME = "stocks.ser";
    private final File file;

    public SerializableStockDAO() throws StorageException {
        this.file = new File(FILE_NAME);
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Stock> loadAll() throws StorageException {
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Stock>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Errore lettura magazzino serializzato", e);
        }
    }

    private void saveAll(List<Stock> stocks) throws StorageException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(stocks);
        } catch (IOException e) {
            throw new StorageException("Errore scrittura magazzino serializzato", e);
        }
    }

    @Override
    public void saveStock(Stock stock) throws StorageException {
        List<Stock> stocks = loadAll();
        stocks.add(stock);
        saveAll(stocks);
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) throws StorageException {
        List<Stock> all = loadAll();
        // Filtriamo in memoria
        if (groupUid == null) return new ArrayList<>();
        return all.stream()
                .filter(s -> groupUid.equals(s.getGroupUid()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException {
        List<Stock> stocks = loadAll();
        for (Stock s : stocks) {
            if (s.getNome().equals(stockName) && groupUid.equals(s.getGroupUid())) {
                s.setQuantity(newQuantity);
                break;
            }
        }
        saveAll(stocks);
    }

    @Override
    public void deleteStock(String stockName, String groupUid) throws StorageException {
        List<Stock> stocks = loadAll();
        stocks.removeIf(s -> s.getNome().equals(stockName) && groupUid.equals(s.getGroupUid()));
        saveAll(stocks);
    }
}