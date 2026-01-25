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
            if (groupUid.equals(s.getGroupUid())) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException {
        for (Stock s : warehouse) {
            if (s.getNome().equals(stockName) && groupUid.equals(s.getGroupUid())) {
                s.setQuantity(newQuantity);
                return;
            }
        }
        throw new StorageException("Errore durante l'aggiornamento della STOCK");
    }

    @Override
    public void deleteStock(String stockName, String groupUid) {
        warehouse.removeIf(s -> s.getNome().equals(stockName) && groupUid.equals(s.getGroupUid()));
    }
}