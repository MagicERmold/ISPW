package com.stocktrack.persistence.dao;

import com.stocktrack.model.Stock;

import java.util.List;

public interface StockDAO {
    void saveStock(Stock stock);
    List<Stock> getAllStocks();
}
