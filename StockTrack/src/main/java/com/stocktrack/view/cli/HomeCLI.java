package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.GroupController;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.controller.ManageUsersController;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

import java.io.IOException;
import java.util.List;

public class HomeCLI {

    private final GroupController groupController = new GroupController();

    public void start() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            System.out.println("Errore: Nessun utente loggato.");
            return;
        }

        System.out.println("\n--- HOME PAGE ---");
        System.out.println("Benvenuto, " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");

        // Gestione Gruppo
        if (currentUser.getGroupUid() == null) {
            handleGroupAssignment(currentUser);
        }

        boolean loggedIn = true;
        while (loggedIn) {
            // Mostriamo opzioni diverse in base al ruolo
            if (currentUser.getRole() == Role.ADMIN) {
                showAdminMenu();
            } else {
                showUserMenu();
            }

            String input = InputHelper.readString("> ");

            // Switch unificato dove possibile o specifico
            switch (input) {
                case "1":
                    new StockCLI().start();
                    break;
                case "2":
                    if (currentUser.getRole() == Role.ADMIN) {
                        manageUsers();
                    } else {
                        generateShoppingList();
                    }
                    break;
                case "3":
                    if (currentUser.getRole() == Role.ADMIN) {
                        generateShoppingList();
                    } else {
                        System.out.println("Opzione non valida.");
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

    private void handleGroupAssignment(User user) {
        System.out.println("\nATTENZIONE: Non appartieni a nessun gruppo.");
        System.out.println("1. Crea un nuovo gruppo famigliare/personale");
        System.out.println("2. Unisciti a un gruppo esistente (serve ID)");

        String choice = InputHelper.readString("> ");

        try {
            if ("1".equals(choice)) {
                String newId = groupController.createGroup();
                System.out.println("Gruppo creato! Il tuo ID Gruppo è: " + newId);
                System.out.println("Condividilo con chi vuoi far accedere al tuo magazzino.");
            } else if ("2".equals(choice)) {
                String groupId = InputHelper.readString("Inserisci ID Gruppo: ");
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
                System.out.printf("%-20s | %-10d | %-10d | %-10d%n", bean.getNome(), bean.getQuantity(), bean.getSoglia(), daOrdinare);
            }
            System.out.println("----------------------------------------------------------\n");

        } catch (IOException e) {
            System.out.println("Errore nel recupero dati: " + e.getMessage());
        }
    }

    private void manageUsers() {
        ManageUsersController userController = new ManageUsersController();
        System.out.println("\n--- GESTIONE UTENTI (TUO GRUPPO) ---");
        try {
            List<User> users = userController.getMyGroupUsers();

            if (users.isEmpty()) {
                System.out.println("Nessun altro utente trovato nel tuo gruppo.");
            } else {
                System.out.printf("%-15s | %-10s | %-15s%n", "USERNAME", "RUOLO", "GRUPPO");
                System.out.println("------------------------------------------------");
                for (User u : users) {
                    System.out.printf("%-15s | %-10s | %-15s%n", u.getUsername(), u.getRole(), (u.getGroupUid() == null ? "N/A" : u.getGroupUid()));
                }
            }

            System.out.println("\nVuoi eliminare un utente? (Scrivi username o premi INVIO per uscire)");
            // Qui usiamo scanner.nextLine() diretto SOLO se vogliamo accettare stringa vuota
            // Ma InputHelper.readString non accetta vuoti.
            // Soluzione: facciamo un metodo ad hoc o usiamo un trucco.
            // Per semplicità qui chiediamo esplicitamente:

            System.out.print("> ");
            // Accediamo allo scanner statico solo se necessario o creiamo metodo in InputHelper allowEmpty
            // Per ora usiamo InputHelper forzando l'utente a scrivere 'esci' o il nome
            String input = InputHelper.readString("Username (o scrivi 'esci'): ");

            if (!input.equalsIgnoreCase("esci")) {
                userController.removeUserFromMyGroup(input);
                System.out.println("Utente rimosso (se esisteva nel gruppo).");
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}