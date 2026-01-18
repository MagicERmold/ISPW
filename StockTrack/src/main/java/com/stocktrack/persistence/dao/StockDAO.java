package com.stocktrack.persistence.dao;

import com.stocktrack.model.Stock;
import java.util.List;

public interface StockDAO {
    void saveStock(Stock stock);
    List<Stock> getAllStocks(String groupUid);
    void updateStockQuantity(String stockName, int newQuantity, String groupUid);

    void deleteStock(String stockName, String groupUid);

    default List<Stock> getAllStocks() { return getAllStocks(null); }
}