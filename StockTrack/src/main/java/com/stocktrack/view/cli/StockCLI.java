package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.exception.InvalidProductDataException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class StockCLI {
    private final ManageStockController controller = new ManageStockController();

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- GESTIONE MAGAZZINO ---");
            System.out.println("1. Aggiungi nuovo prodotto (Nuova Scheda)");
            System.out.println("2. Mostra tutti i prodotti");
            System.out.println("3. Registra Consumo (-)");
            System.out.println("4. Registra Acquisto (+)");
            System.out.println("5. Elimina Prodotto (Rimuovi scheda)");
            System.out.println("0. Torna al menu principale");

            int input = InputHelper.readInt("Scegli un'opzione: ");

            switch (input) {
                case 1:
                    addStock();
                    break;
                case 2:
                    showAllStocks();
                    break;
                case 3:
                    modifyStockQuantity(-1); // Consumo
                    break;
                case 4:
                    modifyStockQuantity(1);  // Acquisto
                    break;
                case 5:
                    deleteStock();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        }
    }

    private void modifyStockQuantity(int sign) {
        String name = InputHelper.readString("Nome prodotto: ");
        int qty = InputHelper.readInt("Quantità da " + (sign > 0 ? "aggiungere" : "rimuovere") + ": ");
        try {
            controller.modifyQuantity(name, qty * sign);
            System.out.println("Operazione completata!");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void addStock() {
        String name = InputHelper.readString("Inserisci nome prodotto: ");
        int quantity = InputHelper.readInt("Inserisci quantità iniziale: ");
        int threshold = InputHelper.readInt("Inserisci soglia minima: ");

        try {
            StockBean bean = new StockBean(name, quantity, threshold);
            controller.addStock(bean);
            System.out.println("Prodotto aggiunto con successo!");
        } catch (InvalidProductDataException e) {
            System.out.println("Errore nei dati: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta: " + e.getMessage());
        }
    }

    private void showAllStocks() {
        try {
            List<StockBean> list = controller.showAllProducts();

            if (list.isEmpty()) {
                System.out.println("Il magazzino è vuoto.");
                return;
            }

            System.out.println("\n------------------------------------------------");
            System.out.printf("%-20s | %-10s | %-10s%n", "NOME", "QUANTITÀ", "SOGLIA");
            System.out.println("------------------------------------------------");

            for (StockBean bean : list) {
                System.out.printf("%-20s | %-10d | %-10d%n", bean.getNome(), bean.getQuantity(), bean.getSoglia());
            }
            System.out.println("------------------------------------------------\n");
        } catch (IOException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }

    private void deleteStock() {
        System.out.println("\n--- ELIMINAZIONE PRODOTTO ---");
        String name = InputHelper.readString("Nome del prodotto da eliminare: ");
        String confirm = InputHelper.readString("Sei sicuro? (s/n): ");

        if ("s".equalsIgnoreCase(confirm)) {
            try {
                controller.deleteProduct(name);
                System.out.println("Prodotto '" + name + "' eliminato con successo.");
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata.");
        }
    }
}