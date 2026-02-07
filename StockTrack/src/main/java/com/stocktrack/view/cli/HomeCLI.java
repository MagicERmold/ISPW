package com.stocktrack.view.cli;

import com.stocktrack.bean.StockBean;
import com.stocktrack.controller.GroupController;
import com.stocktrack.controller.ManageStockController;
import com.stocktrack.controller.ManageUsersController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

import java.util.List;

public class HomeCLI {

    // Istanzio il controller
    private final GroupController groupController = new GroupController();

    public void start() {
        // Recupero l'utente corrente
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Utente non loggato
        if (currentUser == null) {
            InputHelper.print("Errore: Nessun utente loggato.");
            return;
        }

        InputHelper.print("\n--- HOME PAGE ---");
        InputHelper.print("Benvenuto, " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");

        // Gestione Gruppo
        if (currentUser.getGroupId() == null) {
            handleGroupAssignment();
        }

        boolean loggedIn = true;
        Role r =  currentUser.getRole();

        while (loggedIn) {
            // Mostriamo opzioni diverse in base al ruolo
            if (r == Role.ADMIN) {
                showAdminMenu();
            } else {
                showUserMenu();
            }

            // Gestione dell'ingresso
            // Legge input e delega la logica
            String input = InputHelper.readString(">> ");
            loggedIn = processCommand(input, r);
        }
    }

    private void handleGroupAssignment() {
        InputHelper.print("\nATTENZIONE: Non appartieni a nessun gruppo.");
        InputHelper.print("1. Crea un nuovo gruppo famigliare/personale");
        InputHelper.print("2. Unisciti a un gruppo esistente (serve ID)");

        String choice = InputHelper.readString(">> ");

        try {
            if ("1".equals(choice)) {
                String newId = groupController.createGroup();
                InputHelper.print("Gruppo creato! Il tuo ID Gruppo è: " + newId);
                InputHelper.print("Condividilo con chi vuoi far accedere al tuo magazzino.");
            } else if ("2".equals(choice)) {
                String groupId = InputHelper.readString("Inserisci ID Gruppo: ");
                groupController.joinGroup(groupId);
                InputHelper.print("Ti sei unito al gruppo " + groupId);
            }
        } catch (StorageException e) {
            InputHelper.print("Errore salvataggio gruppo: " + e.getMessage());
        }
    }

    private void showAdminMenu() {
        InputHelper.print("\nCosa vuoi fare?");
        InputHelper.print("1. Gestisci Magazzino (Stock)");
        InputHelper.print("2. Gestisci Utenti del Gruppo (Visualizza/Rimuovi)");
        InputHelper.print("3. Genera Lista Spesa (Sottoscorta)");
        InputHelper.print("0. Logout");
    }

    private void showUserMenu() {
        InputHelper.print("\nCosa vuoi fare?");
        InputHelper.print("1. Gestisci Magazzino (Stock)");
        InputHelper.print("2. Genera Lista Spesa (Sottoscorta)");
        InputHelper.print("0. Logout");
    }

    private void generateShoppingList() {
        ManageStockController controller = new ManageStockController();
        InputHelper.print("\n*** GENERAZIONE LISTA SPESA (Prodotti Sottoscorta) ***");

        try {
            List<StockBean> list = controller.getShoppingList();

            if (list.isEmpty()) {
                InputHelper.print("Tutto ok! Nessun prodotto è sotto la soglia minima.");
                return;
            }

            InputHelper.print("ATTENZIONE: I seguenti prodotti sono in esaurimento:");
            InputHelper.print("---------------------------------------------------------- ");
            InputHelper.printf("%-20s | %-10s | %-10s | %-10s%n", "NOME", "QUANTITÀ", "SOGLIA", "DA ORDINARE");
            InputHelper.print("----------------------------------------------------------  ");

            for (StockBean bean : list) {
                int daOrdinare = bean.getThreshold() - bean.getQuantity();
                InputHelper.printf("%-20s | %-10d | %-10d | %-10d%n", bean.getNome(), bean.getQuantity(), bean.getThreshold(), daOrdinare);
            }
            InputHelper.print("----------------------------------------------------------   \n");

        } catch (StorageException e) {
            InputHelper.print("Errore nel recupero dati: " + e.getMessage());
        }
    }

    private void manageUsers() {
        ManageUsersController userController = new ManageUsersController();
        InputHelper.print("\n--- GESTIONE UTENTI (TUO GRUPPO) ---");
        try {
            List<User> users = userController.getMyGroupUsers();

            if (users.isEmpty()) {
                InputHelper.print("Nessun altro utente trovato nel tuo gruppo.");
            } else {
                InputHelper.printf2("%-15s | %-10s | %-15s%n", "USERNAME", "RUOLO", "GRUPPO");
                InputHelper.print("------------------------------------------------");
                for (User u : users) {
                    InputHelper.printf2("%-15s | %-10s | %-15s%n", u.getUsername(), u.getRole(), (u.getGroupId() == null ? "N/A" : u.getGroupId()));
                }
            }

            InputHelper.print("\nVuoi eliminare un utente?");

            InputHelper.print(">> ");

            String input = InputHelper.readString("Username (o scrivi esci): ");

            if (!input.equalsIgnoreCase("esci")) {
                userController.removeUserFromMyGroup(input);
                InputHelper.print("Utente: " + input + ", rimosso.");
            }
        } catch (Exception e) {
            InputHelper.print("Errore: " + e.getMessage());
        }
    }

    private boolean processCommand(String input, Role role) {
        switch (input) {
            case "1":
                new StockCLI().start();
                return true;
            case "2":
                return handleOptionTwo(role);
            case "3":
                return handleOptionThree(role);
            case "0":
                InputHelper.print("Logout effettuato.");
                SessionManager.getInstance().logout();
                return false; // Ferma il ciclo while
            default:
                InputHelper.print("Opzione non valida.");
                return true;
        }
    }

    private boolean handleOptionTwo(Role role) {
        if (role == Role.ADMIN) {
            manageUsers();
        } else {
            generateShoppingList();
        }
        return true;
    }

    private boolean handleOptionThree(Role role) {
        if (role == Role.ADMIN) {
            generateShoppingList();
        } else {
            InputHelper.print("Opzione non valida.");
        }
        return true;
    }
}