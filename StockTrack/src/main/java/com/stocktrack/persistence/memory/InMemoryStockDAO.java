package com.stocktrack.persistence.memory;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.util.ArrayList;
import java.util.List;

public class InMemoryStockDAO implements StockDAO {
    private static final List<Stock> warehouse = new ArrayList<>();

    @Override
    public void saveStock(Stock stock) {
        warehouse.add(stock);
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) {
        List<Stock> result = new ArrayList<>();
        if (groupUid == null) return result;
        for (Stock s : warehouse) {
            if (groupUid.equals(s.getGroupId())) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException {
        for (Stock s : warehouse) {
            if (s.getName().equalsIgnoreCase(stockName) && groupUid.equals(s.getGroupId())) {
                s.setQuantity(newQuantity);
                return;
            }
        }
        throw new StorageException("Errore durante l'aggiornamento della STOCK");
    }

    @Override
    public void updateStockThreshold(String stockName, int newThreshold, String groupUid) throws StorageException {
        for (Stock s : warehouse) {
            if (s.getName().equalsIgnoreCase(stockName) && groupUid.equals(s.getGroupId())) {
                s.setThreshold(newThreshold);
                return;
            }
        }
        throw new StorageException("Errore durante l'aggiornamento della soglia");
    }

    @Override
    public void deleteStock(String stockName, String groupUid) throws StorageException {
        boolean removed = warehouse.removeIf(s -> s.getName().equalsIgnoreCase(stockName) && groupUid.equals(s.getGroupId()));
        if (!removed) {
            throw new StorageException("Prodotto non trovato in memoria");
        }
    }

    @Override
    public List<String> getAllCategories(String groupUid) {
        if(groupUid == null) return new ArrayList<>();

        return warehouse.stream()
                .filter(s -> groupUid.equals(s.getGroupId()))
                .map(Stock::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .toList();
    }

    @Override
    public List<Stock> getStocksByCategory(String groupId, String category){
        List<Stock> result = new ArrayList<>();
        if(groupId == null) return result;
        for (Stock s : warehouse) {
            if (groupId.equals(s.getGroupId()) && s.getCategory() != null && s.getCategory().equalsIgnoreCase(category)) {
                result.add(s);
            }
        }
        return result;
    }
}
