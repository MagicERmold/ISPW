package com.stocktrack.persistence.memory;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            if (s.getName().equals(stockName) && groupUid.equals(s.getGroupId())) {
                s.setQuantity(newQuantity);
                return;
            }
        }
        throw new StorageException("Errore durante l'aggiornamento della STOCK");
    }

    @Override
    public void deleteStock(String stockName, String groupUid) {
        warehouse.removeIf(s -> s.getName().equals(stockName) && groupUid.equals(s.getGroupId()));
    }

    // --- CORREZIONE QUI SOTTO ---
    @Override
    public List<String> getAllCategories(String groupUid) {
        if(groupUid == null) return new ArrayList<>();

        return warehouse.stream()
                .filter(s -> groupUid.equals(s.getGroupId()))  // Prendi solo prodotti del gruppo
                .map(Stock::getCategory)                       // Prendi il nome della categoria
                .filter(c -> c != null && !c.isEmpty())        // Ignora null o vuoti
                .distinct()                                    // <--- RIMUOVE I DUPLICATI
                .toList();
    }

    @Override
    public List<Stock> getStocksByCategory(String groupId, String category){
        List<Stock> result = new ArrayList<>();
        if(groupId == null) return result;
        for (Stock s : warehouse) {
            if (groupId.equals(s.getGroupId()) && category.equals(s.getCategory())) {
                result.add(s);
            }
        }
        return result;
    }
}