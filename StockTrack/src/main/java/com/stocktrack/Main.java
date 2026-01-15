package com.stocktrack;

import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.view.cli.StockCLI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        StockCLI cli = new StockCLI();
        cli.start();
    }
}
