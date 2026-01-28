package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;

import java.io.IOException;
import java.util.List;

public interface StockDAO {
    void saveStock(Stock stock) throws StorageException;
    List<Stock> getAllStocks(String groupUid) throws StorageException;
    void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException, IOException;
    void deleteStock(String stockName, String groupUid) throws StorageException;

    List<String> getAllCategories(String groupId) throws StorageException;
    List<Stock> getStocksByCategory(String groupId, String category) throws StorageException;
}

