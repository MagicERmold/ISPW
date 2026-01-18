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

    private final com.stocktrack.controller.GroupController groupController = new com.stocktrack.controller.GroupController();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            System.out.println("Errore: Nessun utente loggato.");
            return;
        }

        System.out.println("\n--- HOME PAGE ---");
        System.out.println("Benvenuto, " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");

        // SE L'UTENTE NON HA UN GRUPPO, LO FORZIAMO A SCEGLIERNE UNO
        if (currentUser.getGroupUid() == null) {
            handleGroupAssignment(scanner, currentUser);
        }

        boolean loggedIn = true;
        while (loggedIn) {
            // Mostriamo opzioni diverse in base al ruolo
            if (currentUser.getRole() == Role.ADMIN) {
                showAdminMenu();
            } else {
                showUserMenu();
            }

            String input = scanner.nextLine();

            // Gestione input
            if (currentUser.getRole() == Role.ADMIN) {
                // LOGICA ADMIN
                switch (input) {
                    case "1":
                        new StockCLI().start();
                        break;
                    case "2":
                        manageUsers(); // Chiama il metodo aggiornato sotto
                        break;
                    case "3":
                        generateShoppingList();
                        break;
                    case "0":
                        System.out.println("Logout effettuato.");
                        SessionManager.getInstance().logout();
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("Opzione non valida.");
                }
            } else {
                // LOGICA USER STANDARD
                switch (input) {
                    case "1":
                        new StockCLI().start();
                        break;
                    case "2":
                        generateShoppingList();
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
    }

    private void handleGroupAssignment(Scanner scanner, User user) {
        System.out.println("\nATTENZIONE: Non appartieni a nessun gruppo.");
        System.out.println("1. Crea un nuovo gruppo famigliare/personale");
        System.out.println("2. Unisciti a un gruppo esistente (serve ID)");
        System.out.print("> ");
        String choice = scanner.nextLine();

        try {
            if ("1".equals(choice)) {
                String newId = groupController.createGroup();
                System.out.println("Gruppo creato! Il tuo ID Gruppo è: " + newId);
                System.out.println("Condividilo con chi vuoi far accedere al tuo magazzino.");
            } else if ("2".equals(choice)) {
                System.out.print("Inserisci ID Gruppo: ");
                String groupId = scanner.nextLine();
                groupController.joinGroup(groupId);
                System.out.println("Ti sei unito al gruppo " + groupId);
            }
        } catch (IOException e) {
            System.out.println("Errore salvataggio gruppo: " + e.getMessage());
        }
    }

    private void showAdminMenu() {
        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Gestisci Magazzino (Stock)");
        System.out.println("2. Gestisci Utenti del Gruppo (Visualizza/Rimuovi)");
        System.out.println("3. Genera Lista Spesa (Sottoscorta)");
        System.out.println("0. Logout");
        System.out.print("> ");
    }

    private void showUserMenu() {
        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Gestisci Magazzino (Stock)");
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
                        daOrdinare
                );
            }
            System.out.println("----------------------------------------------------------\n");

        } catch (IOException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }

    private void manageUsers() {
        com.stocktrack.controller.ManageUsersController userController = new com.stocktrack.controller.ManageUsersController();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- GESTIONE UTENTI (TUO GRUPPO) ---");
        try {
            // USIAMO IL NUOVO METODO: getMyGroupUsers
            List<User> users = userController.getMyGroupUsers();

            if (users.isEmpty()) {
                System.out.println("Nessun altro utente trovato nel tuo gruppo.");
            } else {
                System.out.printf("%-15s | %-10s | %-15s%n", "USERNAME", "RUOLO", "GRUPPO");
                System.out.println("------------------------------------------------");
                for (User u : users) {
                    System.out.printf("%-15s | %-10s | %-15s%n",
                            u.getUsername(), u.getRole(), (u.getGroupUid() == null ? "N/A" : u.getGroupUid()));
                }
            }

            System.out.println("\nVuoi eliminare un utente del tuo gruppo? (scrivi username o PREMI INVIO per uscire)");
            System.out.print("> ");
            String input = scanner.nextLine();

            if (!input.isEmpty()) {
                // USIAMO IL NUOVO METODO: removeUserFromMyGroup
                userController.removeUserFromMyGroup(input);
                System.out.println("Utente " + input + " rimosso dal tuo gruppo.");
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}