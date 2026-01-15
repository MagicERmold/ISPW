package com.stocktrack.persistence.memory;

import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.util.ArrayList;
import java.util.List;

public class InMemoryStockDAO implements StockDAO {
    private static final List<Stock> warehouse = new ArrayList<>();

    @Override
    public void saveStock(Stock stock) {
        // Simuliamo una INSERT
        warehouse.add(stock);
        System.out.println("Salvataggio in memoria: " + stock.getNome());
    }

    // Da implementare
    @Override
    public List<Stock> getAllStocks() {
        return new ArrayList<>(warehouse);
    }
}
