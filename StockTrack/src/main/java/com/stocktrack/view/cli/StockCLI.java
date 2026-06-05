package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;

import java.util.List;

public class StockCLI {
    private final ManageStockController controller = new ManageStockController();

    public void start() {
        boolean running = true;

        while (running) {
            InputHelper.print("\n--- GESTIONE MAGAZZINO ---");
            InputHelper.print("1. Aggiungi nuovo prodotto (Nuova Scheda)");
            InputHelper.print("2. Mostra tutti i prodotti");
            InputHelper.print("3. Registra Consumo (-)");
            InputHelper.print("4. Registra Acquisto (+)");
            InputHelper.print("5. Elimina Prodotto (Rimuovi scheda)");
            InputHelper.print("6. Modifica soglia minima");
            InputHelper.print("0. Torna al menu principale");

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
                case 6:
                    modifyStockThreshold();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    InputHelper.print("Opzione non valida.");
            }
        }
    }

    private void modifyStockQuantity(int sign) {
        String name = InputHelper.readString("Nome prodotto: ");
        int qty = InputHelper.readInt("Quantità da " + (sign > 0 ? "aggiungere" : "rimuovere") + ": ");
        try {
            controller.modifyQuantity(name, qty * sign);
            InputHelper.print("Operazione completata!");
        } catch (Exception e) {
            InputHelper.print("Errore durante la modifica della Stock: " + e.getMessage());
        }
    }

    private void modifyStockThreshold() {
        String name = InputHelper.readString("Nome prodotto: ");
        int threshold = InputHelper.readInt("Nuova soglia minima: ");
        try {
            controller.modifyThreshold(name, threshold);
            InputHelper.print("Soglia aggiornata con successo!");
        } catch (Exception e) {
            InputHelper.print("Errore durante la modifica della soglia: " + e.getMessage());
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
            InputHelper.print("Prodotto aggiunto con successo!");
        } catch (Exception e) {
            InputHelper.print("Errore durante l'aggiunta della Stock: " + e.getMessage());
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
                    InputHelper.print("Nessuna categoria disponibile.");
                    return;
                }

                InputHelper.print("Categorie disponibili: " + categories);
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
            InputHelper.print("Errore durante la visione delle Stocks: " + e.getMessage());
        }
    }

    private void printTable(List<StockBean> list) {
        if (list.isEmpty()) {
            InputHelper.print("Nessun prodotto trovato.");
            return;
        }
        InputHelper.print("---------------------------------------------------------------------- ");
        InputHelper.printf("%-15s | %-15s | %-10s | %-10s%n", "NOME", "CATEGORIA", "QUANTITÀ", "SOGLIA");
        InputHelper.print("----------------------------------------------------------------------  ");
        for (StockBean b : list) {
            InputHelper.printf("%-15s | %-15s | %-10d | %-10d%n",
                    b.getNome(), b.getCategory(), b.getQuantity(), b.getThreshold());
        }
        InputHelper.print("----------------------------------------------------------------------   ");
    }

    private void deleteStock() {
        InputHelper.print("\n--- ELIMINAZIONE PRODOTTO ---");
        String name = InputHelper.readString("Nome del prodotto da eliminare: ");
        String confirm = InputHelper.readString("Sei sicuro? (s/n): ");

        if ("s".equalsIgnoreCase(confirm)) {
            try {
                controller.deleteStock(name);
                InputHelper.print("Prodotto '" + name + "' eliminato con successo.");
            } catch (Exception e) {
                InputHelper.print("Errore durante l'eliminazione del prodotto: " + e.getMessage());
            }
        } else {
            InputHelper.print("Operazione annullata.");
        }
    }
}
