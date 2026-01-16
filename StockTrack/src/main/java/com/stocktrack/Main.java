package com.stocktrack;

import com.stocktrack.view.cli.StockCLI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        StockCLI cli = new StockCLI();
        cli.start();
    }
}
