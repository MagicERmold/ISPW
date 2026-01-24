package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Stock;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageStockController {

    // ADMIN: Aggiunge nuovo prodotto (MODIFICATO PER EVITARE DUPLICATI)
    public void addStock(StockBean bean) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getGroupUid() == null) throw new Exception("Devi prima creare o unirti a un gruppo!");

        StockDAO dao = DAOFactory.getStockDAO();

        // 1. Controlliamo se il prodotto esiste già
        List<Stock> existingStocks = dao.getAllStocks(user.getGroupUid());
        for (Stock s : existingStocks) {
            // Confronto Case-Insensitive (Acqua == acqua)
            if (s.getNome().equalsIgnoreCase(bean.getNome())) {
                throw new Exception("Il prodotto '" + bean.getNome() + "' esiste già! Usa 'Registra Acquisto' per aggiornare la quantità.");
            }
        }

        // 2. Se non esiste, lo salviamo
        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getSoglia(), user.getGroupUid());
        dao.saveStock(stock);
    }

    // USER & ADMIN: Consuma (diminuisce) o Acquista (aumenta)
    public void modifyQuantity(String productName, int amountChange) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Cerco il prodotto attuale per sapere la quantità corrente
        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());
        Stock target = null;
        for(Stock s : stocks) {
            if(s.getNome().equalsIgnoreCase(productName)) { // Meglio usare equalsIgnoreCase anche qui
                target = s; break;
            }
        }

        if(target == null) throw new Exception("Prodotto non trovato!");

        int newQty = target.getQuantity() + amountChange;
        if (newQty < 0) throw new Exception("Non puoi avere quantità negativa!");

        dao.updateStockQuantity(target.getNome(), newQty, user.getGroupUid());
    }

    public List<StockBean> showAllProducts() throws StorageException, IOException {
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());

        List <StockBean> stockBeans = new ArrayList<>();
        for (Stock stock : stocks) {
            stockBeans.add(new StockBean(stock.getNome(), stock.getQuantity(), stock.getSoglia()));
        }
        return stockBeans;
    }

    public List<StockBean> getShoppingList() throws IOException, StorageException {
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        List<Stock> allStocks = dao.getAllStocks(user.getGroupUid());

        List<StockBean> shoppingList = new ArrayList<>();
        for (Stock stock : allStocks) {
            if (stock.getQuantity() < stock.getSoglia()) {
                shoppingList.add(new StockBean(stock.getNome(), stock.getQuantity(), stock.getSoglia()));
            }
        }
        return shoppingList;
    }

    public void deleteProduct(String productName) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Verifica che il prodotto esista prima di cancellarlo
        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());
        boolean exists = false;
        for (Stock s : stocks) {
            if (s.getNome().equalsIgnoreCase(productName)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            throw new Exception("Prodotto non trovato nel tuo magazzino.");
        }

        dao.deleteStock(productName, user.getGroupUid());
    }
}