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
    public void addStock(StockBean bean) throws StorageException {
        // Recupero l'utente e controllo se appartiene a un gruppo per sicurezza
        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getGroupUid() == null) {
            throw new StorageException("Devi prima creare o unirti a un gruppo!");
        }

        // Recupero la persistenza
        StockDAO dao = DAOFactory.getStockDAO();

        // Controllo se il prodotto esiste già
        List<Stock> existingStocks = dao.getAllStocks(user.getGroupUid());
        for (Stock s : existingStocks) {
            // Confronto Case-Insensitive (Acqua == acqua)
            if (s.getNome().equalsIgnoreCase(bean.getNome())) {
                throw new StorageException("Il prodotto '" + bean.getNome() + "' esiste già! Usa 'Registra Acquisto' per aggiornare la quantità.");
            }
        }

        // Se non esiste, creo la STOCK e la salvo nel database
        Stock stock = new Stock(bean.getNome(), bean.getQuantity(), bean.getSoglia(), user.getGroupUid());
        dao.saveStock(stock);
    }

    // USER & ADMIN: Consuma (diminuisce) o Acquista (aumenta)
    public void modifyQuantity(String productName, int amountChange) throws StorageException {
        // Recupero l'utente e il metodo di persistenza
        User user = SessionManager.getInstance().getCurrentUser();
        StockDAO dao = DAOFactory.getStockDAO();

        // Cerco il prodotto attuale per sapere la quantità corrente
        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());
        Stock target = null;
        for(Stock s : stocks) {
            if(s.getNome().equalsIgnoreCase(productName)) { // Meglio usare equalsIgnoreCase anche qui
                target = s;
                break;
            }
        }

        // Il prodotto non è stato trovato
        if(target == null) throw new StorageException("Prodotto non trovato!");

        // Il prodotto è stato trovato
        int newQty = target.getQuantity() + amountChange;
        if (newQty < 0) throw new StorageException("Non puoi avere quantità negativa!");

        // Aggiorno la quantità in memoria
        dao.updateStockQuantity(target.getNome(), newQty, user.getGroupUid());
    }

    // ADMIN & USER: recupero la lista dei prodotti
    // getAllStocks ha bisogno delle exceptions
    public List<StockBean> showAllProducts() throws StorageException, IOException {
        // Recupero utente e il metodo di persistenza
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        // Recupero tutte le STOCKS
        List<Stock> stocks = dao.getAllStocks(user.getGroupUid());

        // Creo una lista di STOCKBEAN da restituire alla boundary per creare la tabella
        List <StockBean> stockBeans = new ArrayList<>();
        for (Stock stock : stocks) {
            stockBeans.add(new StockBean(stock.getNome(), stock.getQuantity(), stock.getSoglia()));
        }
        return stockBeans;
    }

    // ADMIN & USER: genero la lista di prodotti sotto scorta
    // getAllStocks ha bisogno delle exceptions
    public List<StockBean> getShoppingList() throws IOException, StorageException {
        // Recupero utente e il metodo di persistenza
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionManager.getInstance().getCurrentUser();

        // Recupero tutte le STOCKS
        List<Stock> allStocks = dao.getAllStocks(user.getGroupUid());

        // Creo la lista e controllo quali STOCK hanno la quantity < soglia
        List<StockBean> shoppingList = new ArrayList<>();
        for (Stock stock : allStocks) {
            if (stock.getQuantity() < stock.getSoglia()) {
                shoppingList.add(new StockBean(stock.getNome(), stock.getQuantity(), stock.getSoglia()));
            }
        }
        return shoppingList;
    }

    // ADMIN: cancella STOCK dalla memoria
    public void deleteProduct(String productName) throws StorageException {
        // Recupero utente e il metodo di persistenza
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

        // Il prodotto non esiste
        if (!exists) {
            throw new StorageException("Prodotto non trovato nel tuo magazzino.");
        }

        // La dao si occuperà di cancellare la STOCK
        dao.deleteStock(productName, user.getGroupUid());
    }
}