package com.stocktrack.controller;

import com.stocktrack.bean.StockBean;
import com.stocktrack.engineering.exception.InvalidProductDataException;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.Stock;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.StockDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller applicativo per la gestione del magazzino del gruppo corrente.
 * Valida i dati ricevuti dalla Boundary, coordina i DAO e registra le attivita principali.
 */
public class ManageStockController {
    private static final String ACTIVITY_TYPE_WAREHOUSE = "MAGAZZINO";
    private final ActivityLogController activityLogController = new ActivityLogController();

    /**
     * Aggiunge un nuovo prodotto al magazzino del gruppo dell'utente corrente.
     *
     * @param bean dati del prodotto inseriti dalla Boundary
     * @throws StorageException se la sessione o la persistenza non sono disponibili
     * @throws InvalidProductDataException se i dati del prodotto non sono validi
     */
    public void addStock(StockBean bean) throws StorageException, InvalidProductDataException {
        // Recupero l'utente e controllo se appartiene a un gruppo per sicurezza
        User user = SessionGuard.requireUserWithGroup();

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
        activityLogController.recordActivity(ACTIVITY_TYPE_WAREHOUSE,
                "ha aggiunto " + bean.getQuantity() + " unita di " + bean.getNome());
    }

    /**
     * Recupera le categorie presenti nel magazzino del gruppo corrente.
     */
    public List<String> getCategories() throws StorageException {
        User user = SessionGuard.requireUserWithGroup();
        return DAOFactory.getStockDAO().getAllCategories(user.getGroupId());
    }

    /**
     * Recupera i prodotti del gruppo corrente filtrati per categoria.
     */
    public List<StockBean> getStocksByCategory(String category) throws StorageException {
        User user = SessionGuard.requireUserWithGroup();
        List<Stock> stocks = DAOFactory.getStockDAO().getStocksByCategory(user.getGroupId(), category);

        List<StockBean> beans = new ArrayList<>();
        for (Stock s : stocks) {
            beans.add(new StockBean(s.getName(), s.getQuantity(), s.getThreshold(), s.getCategory()));
        }
        return beans;
    }

    /**
     * Modifica la quantita di un prodotto registrando acquisti o consumi.
     *
     * @param productName nome del prodotto da aggiornare
     * @param amountChange variazione da applicare, positiva per acquisto e negativa per consumo
     * @throws StorageException se il prodotto non esiste o la persistenza non e disponibile
     * @throws InvalidProductDataException se la variazione porterebbe la quantita sotto zero
     */
    public void modifyQuantity(String productName, int amountChange) throws StorageException, InvalidProductDataException {
        User user = SessionGuard.requireUserWithGroup();
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
        String action = amountChange > 0 ? "ha comprato " : "ha usato ";
        activityLogController.recordActivity(ACTIVITY_TYPE_WAREHOUSE,
                action + Math.abs(amountChange) + " unita di " + target.getName());
    }

    /**
     * Restituisce tutti i prodotti del magazzino associato al gruppo corrente.
     */
    public List<StockBean> showAllStocks() throws StorageException {
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionGuard.requireUserWithGroup();

        List<Stock> stocks = dao.getAllStocks(user.getGroupId());

        List <StockBean> stockBeans = new ArrayList<>();
        for (Stock stock : stocks) {
            stockBeans.add(new StockBean(stock.getName(), stock.getQuantity(), stock.getThreshold(), stock.getCategory()));
        }
        return stockBeans;
    }

    /**
     * Genera la lista della spesa includendo i prodotti sotto la rispettiva soglia.
     */
    public List<StockBean> getShoppingList() throws StorageException {
        // Recupero utente e il metodo di persistenza
        StockDAO dao = DAOFactory.getStockDAO();
        User user = SessionGuard.requireUserWithGroup();

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

    /**
     * Elimina un prodotto dal magazzino del gruppo corrente.
     *
     * @param productName nome del prodotto da cancellare
     * @throws StorageException se il prodotto non esiste o la persistenza non e disponibile
     */
    public void deleteStock(String productName) throws StorageException {
        User user = SessionGuard.requireUserWithGroup();
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
        activityLogController.recordActivity(ACTIVITY_TYPE_WAREHOUSE, "ha eliminato il prodotto " + productName);
    }
}
