package com.stocktrack.persistence.memory;

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
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) {
        for (Stock s : warehouse) {
            if (s.getNome().equals(stockName) &&
                    (groupUid == null ? s.getGroupUid() == null : groupUid.equals(s.getGroupUid()))) {
                s.setQuantity(newQuantity);
                return;
            }
        }
    }
}