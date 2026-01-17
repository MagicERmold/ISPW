package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.exception.InvalidProductDataException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageStockController {

    // --- METODO AGGIORNATO ---
    public void addStock(StockBean bean) throws IOException, InvalidProductDataException {
        // Validazione dei dati (Business Logic)
        if (bean.getQuantity() < 0) {
            throw new InvalidProductDataException("La quantità non può essere negativa.");
        }
        if (bean.getSoglia() < 0) {
            throw new InvalidProductDataException("La soglia minima non può essere negativa.");
        }
        if (bean.getNome() == null || bean.getNome().trim().isEmpty()) {
            throw new InvalidProductDataException("Il nome del prodotto non può essere vuoto.");
        }

        // Se i controlli passano, procediamo
        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getSoglia());
        StockDAO dao = DAOFactory.getStockDAO();
        dao.saveStock(stock);
    }

    public List<StockBean> showAllProducts() throws IOException {
        // Recupero il DAO
        StockDAO dao = DAOFactory.getStockDAO();

        // Recupero le entity
        List<Stock> stocks = dao.getAllStocks();

        // Converto entity in bean
        List <StockBean> stockBeans = new ArrayList<>();

        for (Stock stock : stocks) {
            StockBean bean = new StockBean(
                    stock.getNome(),
                    stock.getQuantity(),
                    stock.getSoglia()
            );
            stockBeans.add(bean);
        }

        return stockBeans;
    }

    public List<StockBean> getShoppingList() throws IOException {
        StockDAO dao = DAOFactory.getStockDAO();
        List<Stock> allStocks = dao.getAllStocks();

        List<StockBean> shoppingList = new ArrayList<>();

        for (Stock stock : allStocks) {
            // Logica di Business: Se la quantità è INFERIORE alla soglia, serve comprare!
            if (stock.getQuantity() < stock.getSoglia()) {
                StockBean bean = new StockBean(
                        stock.getNome(),
                        stock.getQuantity(),
                        stock.getSoglia()
                );
                shoppingList.add(bean);
            }
        }
        return shoppingList;
    }
}
