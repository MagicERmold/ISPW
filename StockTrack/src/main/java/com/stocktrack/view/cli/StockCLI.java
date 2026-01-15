package com.stocktrack.view.cli;

import java.util.Scanner;

public class StockCLI {
    public void start() {
        Scanner scanner = new Scanner(System.in);

        // Stampo il menù a schermo
        System.out.println("HOME MENU:");
        System.out.println("1. Add product");
        System.out.println("2. Update product");
        System.out.println("3. Delete product");

        String name = scanner.nextLine();

    }
}
