package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class StockCLI {
    private final ManageStockController controller = new ManageStockController();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- HOME MENU ---"); // Qui System.out è OK (è la View!)
            System.out.println("1. Add product");
            System.out.println("2. Show all products");
            System.out.println("0. Exit");
            System.out.print("Scegli un'opzione: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    addStock(scanner);
                    break;
                case "2":
                    // Qui dovrai implementare la visualizzazione (chiamando il controller)
                    showAllProducts();
                    break;
                case "0":
                    running = false;
                    System.out.println("Arrivederci!");
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        }

    }

    private void addStock(Scanner scanner) {
        System.out.print("Inserisci nome prodotto: ");
        String name = scanner.nextLine();

        System.out.print("Inserisci quantità: ");
        int quantity = Integer.parseInt(scanner.nextLine()); // Gestire eccezioni NumberFormat sarebbe meglio

        System.out.print("Inserisci soglia minima: ");
        int threshold = Integer.parseInt(scanner.nextLine());

        // Creo il Bean (trasporto dati)
        StockBean bean = new StockBean(name, quantity, threshold);

        try {
            // Passo il bean al controller
            controller.addStock(bean);
            System.out.println("Prodotto aggiunto con successo!");
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta: " + e.getMessage());
        }
    }

    private void showAllProducts() {
        try {
            // Chiamo il controller
            List<StockBean> list = controller.showAllProducts();

            if (list.isEmpty()) {
                System.out.println("Il magazzino è vuoto.");
                return;
            }

            // Intestazione tabella (formattazione carina per la CLI)
            System.out.println("\n------------------------------------------------");
            // %-20s significa "stringa allineata a sinistra, spazio 20 char"
            System.out.printf("%-20s | %-10s | %-10s%n", "NOME", "QUANTITÀ", "SOGLIA");
            System.out.println("------------------------------------------------");

            for (StockBean bean : list) {
                System.out.printf("%-20s | %-10d | %-10d%n",
                        bean.getNome(),
                        bean.getQuantity(),
                        bean.getSoglia()
                );
            }

            System.out.println("------------------------------------------------\n");
        } catch (IOException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }
}
