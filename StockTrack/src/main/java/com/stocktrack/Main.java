package com.stocktrack;

import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Chiedo il DAO alla Factory (lei leggerà "DEMO" dal file)
        StockDAO dao = DAOFactory.getStockDAO();
        System.out.println("Il DAO creato è di tipo: " + dao.getClass().getSimpleName());

        // Provo a salvare
        Stock stock = new Stock("Pasta", 10, 2);
        dao.saveStock(stock);

        // 3. Verifico se è rimasto in memoria
        StockDAO dao2 = DAOFactory.getStockDAO();
        System.out.println("Prodotti nel magazzino: " + dao2.getAllStocks().size());


    }
}
