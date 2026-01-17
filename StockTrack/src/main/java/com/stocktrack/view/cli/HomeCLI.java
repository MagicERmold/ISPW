package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class HomeCLI {

    public void start() {
        Scanner scanner = new Scanner(System.in);
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            System.out.println("Errore: Nessun utente loggato.");
            return;
        }

        System.out.println("\n--- HOME PAGE ---");
        System.out.println("Benvenuto, " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");

        boolean loggedIn = true;
        while (loggedIn) {
            // Mostriamo opzioni diverse in base al ruolo
            if (currentUser.getRole() == Role.ADMIN) {
                showAdminMenu();
            } else {
                showUserMenu();
            }

            String input = scanner.nextLine();

            // Gestione input unificata o specifica
            // Per semplicità qui gestiamo le azioni comuni e deleghiamo
            switch (input) {
                case "1":
                    // Sia Admin che User possono gestire il magazzino
                    // Lanciamo la vecchia StockCLI che gestisce i prodotti
                    StockCLI stockCLI = new StockCLI();
                    stockCLI.start(); // Nota: dovrai modificare StockCLI per non avere il loop infinito se vuoi tornare qui
                    break;

                case "2":
                    if (currentUser.getRole() == Role.ADMIN) {
                        System.out.println("Funzionalità 'Gestione Utenti' non ancora implementata.");
                    } else {
                        generateShoppingList();
                    }
                    break;

                case "0":
                    System.out.println("Logout effettuato.");
                    SessionManager.getInstance().logout();
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Opzione non valida.");
            }
        }
    }

    private void showAdminMenu() {
        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Gestisci Magazzino (Stock)");
        System.out.println("2. Gestisci Utenti (Aggiungi/Rimuovi)");
        System.out.println("0. Logout");
        System.out.print("> ");
    }

    private void showUserMenu() {
        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Visualizza/Modifica Magazzino");
        System.out.println("2. Genera Lista Spesa (Sottoscorta)");
        System.out.println("0. Logout");
        System.out.print("> ");
    }

    private void generateShoppingList() {
        ManageStockController controller = new ManageStockController();
        System.out.println("\n*** GENERAZIONE LISTA SPESA (Prodotti Sottoscorta) ***");

        try {
            List<StockBean> list = controller.getShoppingList();

            if (list.isEmpty()) {
                System.out.println("Tutto ok! Nessun prodotto è sotto la soglia minima.");
                return;
            }

            System.out.println("ATTENZIONE: I seguenti prodotti sono in esaurimento:");
            System.out.println("----------------------------------------------------------");
            System.out.printf("%-20s | %-10s | %-10s | %-10s%n", "NOME", "QUANTITÀ", "SOGLIA", "DA ORDINARE");
            System.out.println("----------------------------------------------------------");

            for (StockBean bean : list) {
                int daOrdinare = bean.getSoglia() - bean.getQuantity();
                System.out.printf("%-20s | %-10d | %-10d | %-10d%n",
                        bean.getNome(),
                        bean.getQuantity(),
                        bean.getSoglia(),
                        daOrdinare // Suggeriamo quanti comprarne per tornare almeno a pari
                );
            }
            System.out.println("----------------------------------------------------------\n");

        } catch (IOException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }
}