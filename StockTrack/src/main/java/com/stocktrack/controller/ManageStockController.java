package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageStockController {

    public void addStock(StockBean bean) throws IOException {
        // 1. Converti Bean in Entity
        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getSoglia()); // Andrò ad inserire i metodi bean come parametri

        // 2. Ottieni il DAO (non sa se è Demo o Full!)
        StockDAO dao = DAOFactory.getStockDAO();

        // 3. Salva
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
