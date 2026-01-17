package com.stocktrack.persistence.dao;

import com.stocktrack.model.Stock;
import java.util.List;

public interface StockDAO {
    void saveStock(Stock stock);
    List<Stock> getAllStocks(String groupUid); // ORA RICHIEDE IL GRUPPO
    void updateStockQuantity(String stockName, int newQuantity, String groupUid); // NUOVO

    // Mantieni questo per retrocompatibilità se serve, ma fallo ritornare lista vuota o null
    default List<Stock> getAllStocks() { return getAllStocks(null); }
}