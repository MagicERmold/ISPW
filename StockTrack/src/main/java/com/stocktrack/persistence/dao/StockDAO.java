package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;

import java.util.List;

/**
 * Contratto di persistenza per i prodotti del magazzino.
 * Ogni implementazione deve mantenere le operazioni isolate per gruppo.
 */
public interface StockDAO {
    /**
     * Salva un nuovo prodotto.
     */
    void saveStock(Stock stock) throws StorageException;

    /**
     * Recupera tutti i prodotti associati al gruppo indicato.
     */
    List<Stock> getAllStocks(String groupUid) throws StorageException;

    /**
     * Aggiorna la quantita di un prodotto del gruppo indicato.
     */
    void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException;

    /**
     * Aggiorna la soglia minima di un prodotto del gruppo indicato.
     */
    void updateStockThreshold(String stockName, int newThreshold, String groupUid) throws StorageException;

    /**
     * Elimina un prodotto del gruppo indicato.
     */
    void deleteStock(String stockName, String groupUid) throws StorageException;

    /**
     * Recupera le categorie disponibili nel gruppo indicato.
     */
    List<String> getAllCategories(String groupId) throws StorageException;

    /**
     * Recupera i prodotti del gruppo filtrandoli per categoria.
     */
    List<Stock> getStocksByCategory(String groupId, String category) throws StorageException;
}
