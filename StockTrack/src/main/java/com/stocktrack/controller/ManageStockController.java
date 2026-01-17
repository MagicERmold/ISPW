package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.exception.InvalidProductDataException;
import com.stocktrack.model.Stock;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageStockController {

    // ADMIN: Aggiunge nuovo prodotto
    public void addStock(StockBean bean) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getGroupUid() == null) throw new Exception("Devi prima creare o unirti a un gruppo!");

        // Passa il GroupUID al nuovo stock
        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getSoglia(), user.getGroupUid());
        DAOFactory.getStockDAO().saveStock(stock);
    }

    // USER: Consuma (diminuisce) o Acquista (aumenta)
    public void modifyQuantity(String productName, int amountChange) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Cerco il prodotto attuale per sapere la quantità corrente
        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());
        Stock target = null;
        for(Stock s : stocks) {
            if(s.getNome().equals(productName)) {
                target = s; break;
            }
        }

        if(target == null) throw new Exception("Prodotto non trovato!");

        int newQty = target.getQuantity() + amountChange;
        if (newQty < 0) throw new Exception("Non puoi avere quantità negativa!");

        dao.updateStockQuantity(productName, newQty, user.getGroupUid());
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
