package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.exception.InvalidProductDataException;
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

    // Aggiunta nuova STOCK nel magazzino
    public void addStock(StockBean bean) throws StorageException, InvalidProductDataException {
        // Recupero l'utente e controllo se appartiene a un gruppo per sicurezza
        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getGroupId() == null) {
            throw new StorageException("Devi prima creare o unirti a un gruppo!");
        }

        // VALIDAZIONE DATI
        if (bean.getNome() == null || bean.getNome().trim().isEmpty()) {
            throw new InvalidProductDataException("Il nome del prodotto non può essere vuoto.");
        }
        if (bean.getQuantity() < 0) {
            throw new InvalidProductDataException("La quantità iniziale non può essere negativa.");
        }
        if (bean.getThreshold() < 0) {
            throw new InvalidProductDataException("La soglia non può essere negativa.");
        }

        // Recupero la persistenza
        StockDAO dao = DAOFactory.getStockDAO();

        // Controllo se il prodotto esiste già
        List<Stock> existingStocks = dao.getAllStocks(user.getGroupId());
        for (Stock s : existingStocks) {
            if (s.getName().equalsIgnoreCase(bean.getNome())) {
                throw new InvalidProductDataException("Il prodotto '" + bean.getNome() + "' esiste già!");
            }
        }

        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getThreshold(), user.getGroupId(), bean.getCategory());
        dao.saveStock(stock);
    }

    // Recupera categorie uniche
    public List<String> getCategories() throws StorageException {
        User user = SessionManager.getInstance().getCurrentUser();
        return DAOFactory.getStockDAO().getAllCategories(user.getGroupId());
    }

    // Filtra STOCKS e restituisce la lista delle STOCKS filtrate per categoria
    public List<StockBean> getStocksByCategory(String category) throws StorageException {
        User user = SessionManager.getInstance().getCurrentUser();
        List<Stock> stocks = DAOFactory.getStockDAO().getStocksByCategory(user.getGroupId(), category);

        List<StockBean> beans = new ArrayList<>();
        for (Stock s : stocks) {
            beans.add(new StockBean(s.getName(), s.getQuantity(), s.getThreshold(), s.getCategory()));
        }
        return beans;
    }

    // Consuma (diminuisce) o Acquista (aumenta) STOCKS
    public void modifyQuantity(String productName, int amountChange) throws StorageException, InvalidProductDataException, IOException {
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Cerco il prodotto attuale per sapere la quantità corrente
        List<Stock> stocks = dao.getAllStocks(user.getGroupId());
        Stock target = null;
        for(Stock s : stocks) {
            if(s.getName().equalsIgnoreCase(productName)) {
                target = s;
                break;
            }
        }

        // Il prodotto non è stato trovato
        if(target == null) {
            throw new StorageException("Prodotto non trovato!");
        }

        // Il prodotto è stato trovato
        int newQty = target.getQuantity() + amountChange;
        // VALIDAZIONE
        if (newQty < 0) {
            throw new InvalidProductDataException("Operazione non valida: la quantità diventerebbe negativa (" + newQty + ").");
        }

        // Aggiorno la quantità in memoria
        dao.updateStockQuantity(target.getName(), newQty, user.getGroupId());
    }

    // Recupero tutte le STOCKS dal database
    public List<StockBean> showAllStocks() throws StorageException {
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        List<Stock> stocks = dao.getAllStocks(user.getGroupId());

        List <StockBean> stockBeans = new ArrayList<>();
        for (Stock stock : stocks) {
            stockBeans.add(new StockBean(stock.getName(), stock.getQuantity(), stock.getThreshold(), stock.getCategory()));
        }
        return stockBeans;
    }

    // Genero la lista di prodotti sotto scorta
    public List<StockBean> getShoppingList() throws StorageException, IOException {
        // Recupero utente e il metodo di persistenza
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        // Recupero tutte le STOCKS
        List<Stock> allStocks = dao.getAllStocks(user.getGroupId());

        // Creo la lista e controllo quali STOCK hanno la quantity < soglia
        List<StockBean> shoppingList = new ArrayList<>();
        for (Stock stock : allStocks) {
            if (stock.getQuantity() < stock.getThreshold()) {
                shoppingList.add(new StockBean(stock.getName(), stock.getQuantity(), stock.getThreshold(), stock.getCategory()));
            }
        }
        return shoppingList;
    }

    // Cancella STOCK dal database
    public void deleteStock(String productName) throws StorageException {
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Verifica che il prodotto esista prima di cancellarlo
        List<Stock> stocks = dao.getAllStocks(user.getGroupId());
        boolean exists = false;
        for (Stock s : stocks) {
            if (s.getName().equalsIgnoreCase(productName)) {
                exists = true;
                break;
            }
        }

        // Il prodotto non esiste
        if (!exists) {
            throw new StorageException("Prodotto non trovato nel tuo magazzino.");
        }

        // La dao si occuperà di cancellare la STOCK
        dao.deleteStock(productName, user.getGroupId());
    }
}