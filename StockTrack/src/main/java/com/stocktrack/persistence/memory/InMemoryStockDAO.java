package com.stocktrack.persistence.memory;

import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InMemoryStockDAO implements StockDAO {
    private static final List<Stock> warehouse = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(InMemoryStockDAO.class.getName());

    @Override
    public void saveStock(Stock stock) {
        // Simuliamo una INSERT
        warehouse.add(stock);
        logger.info(() -> "Salvataggio in memoria effettuato: " + stock.getNome());
    }

    // Da implementare
    @Override
    public List<Stock> getAllStocks() {
        return new ArrayList<>(warehouse);
    }
}
