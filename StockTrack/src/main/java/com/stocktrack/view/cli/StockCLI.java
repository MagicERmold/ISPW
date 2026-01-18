package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.engineering.exception.InvalidProductDataException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class StockCLI {
    private final ManageStockController controller = new ManageStockController();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        // Non serve più recuperare l'utente qui per differenziare il menu,
        // perché ora le opzioni sono uguali per tutti.

        while (running) {
            System.out.println("\n--- GESTIONE MAGAZZINO ---");

            // Menu Unificato per Admin e User
            System.out.println("1. Aggiungi nuovo prodotto (Nuova Scheda)");
            System.out.println("2. Mostra tutti i prodotti");
            System.out.println("3. Registra Consumo (-)");
            System.out.println("4. Registra Acquisto (+)");
            System.out.println("5. Elimina Prodotto (Rimuovi scheda)");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scegli un'opzione: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    addStock(scanner);
                    break;
                case "2":
                    showAllStocks();
                    break;
                case "3":
                    modifyStockQuantity(scanner, -1); // Consumo
                    break;
                case "4":
                    modifyStockQuantity(scanner, 1);  // Acquisto
                    break;
                case "5":
                    deleteStock(scanner);
                    break;
                case "0":
                    System.out.println("Torno al menu principale...");
                    running = false;
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        }
    }

    private void modifyStockQuantity(Scanner scanner, int sign) {
        System.out.print("Nome prodotto: ");
        String name = scanner.nextLine();
        System.out.print("Quantità da " + (sign > 0 ? "aggiungere" : "rimuovere") + ": ");
        try {
            String qtyStr = scanner.nextLine();
            int qty = Integer.parseInt(qtyStr);
            // Moltiplichiamo per il segno (se consumo, qty diventa negativa)
            controller.modifyQuantity(name, qty * (sign > 0 ? 1 : -1));
            System.out.println("Operazione completata!");
        } catch (NumberFormatException e) {
            System.out.println("Errore: Inserisci un numero valido.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void addStock(Scanner scanner) {
        System.out.print("Inserisci nome prodotto: ");
        String name = scanner.nextLine();

        try {
            System.out.print("Inserisci quantità iniziale: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Inserisci soglia minima: ");
            int threshold = Integer.parseInt(scanner.nextLine());

            StockBean bean = new StockBean(name, quantity, threshold);
            controller.addStock(bean);
            System.out.println("Prodotto aggiunto con successo!");

        } catch (NumberFormatException e) {
            System.out.println("Errore: Devi inserire dei numeri per quantità e soglia.");
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

    private void deleteStock(Scanner scanner) {
        System.out.println("\n--- ELIMINAZIONE PRODOTTO ---");
        System.out.print("Nome del prodotto da eliminare definitivamente: ");
        String name = scanner.nextLine();

        System.out.print("Sei sicuro? (s/n): ");
        String confirm = scanner.nextLine();

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