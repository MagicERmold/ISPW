package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;

import java.util.List;

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
        String category = InputHelper.readString("Categoria (es. Cibo, Elettronica): ");

        try {
            StockBean bean = new StockBean(name, quantity, threshold, category);
            controller.addStock(bean);
            System.out.println("Prodotto aggiunto con successo!");
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta: " + e.getMessage());
        }
    }

    private void showAllStocks() {
        try {
            String choice = InputHelper.readString("Vuoi filtrare per categoria? (y/n): ");
            List<StockBean> list;

            if (choice.equalsIgnoreCase("y")) {
                // 1. Mostra categorie disponibili
                List<String> categories = controller.getCategories();
                if (categories.isEmpty()) {
                    System.out.println("Nessuna categoria disponibile.");
                    return;
                }

                System.out.println("Categorie disponibili: " + categories);
                String catChoice = InputHelper.readString("Inserisci nome categoria esatto: ");

                // 2. Recupera filtrati
                list = controller.getStocksByCategory(catChoice);
            } else {
                // 3. Recupera tutti
                list = controller.showAllStocks();
            }

            // 4. Stampa tabella
            printTable(list);

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void printTable(List<StockBean> list) {
        if (list.isEmpty()) {
            System.out.println("Nessun prodotto trovato.");
            return;
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-10s | %-10s%n", "NOME", "CATEGORIA", "QUANTITÀ", "SOGLIA");
        System.out.println("----------------------------------------------------------------------");
        for (StockBean b : list) {
            System.out.printf("%-15s | %-15s | %-10d | %-10d%n",
                    b.getNome(), b.getCategory(), b.getQuantity(), b.getThreshold());
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private void deleteStock() {
        System.out.println("\n--- ELIMINAZIONE PRODOTTO ---");
        String name = InputHelper.readString("Nome del prodotto da eliminare: ");
        String confirm = InputHelper.readString("Sei sicuro? (s/n): ");

        if ("s".equalsIgnoreCase(confirm)) {
            try {
                controller.deleteStock(name);
                System.out.println("Prodotto '" + name + "' eliminato con successo.");
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata.");
        }
    }
}