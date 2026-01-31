package com.stocktrack.view.cli;

import java.util.Scanner;

public class InputHelper {

    // Unica istanza di Scanner per tutta l'app
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Legge una stringa non vuota.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Errore: L'input non può essere vuoto. Riprova.");
        }
    }

    /**
     * Legge un intero in modo robusto.
     * Evita il crash se l'utente inserisce testo e pulisce il buffer correttamente.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Errore: Devi inserire un numero intero valido. Riprova.");
            }
        }
    }

    public static String readUsername(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (input.length() > 3) {
                return input;
            }
            System.out.println("ERRORE: Username troppo corto.");
        }
    }

    public static String readPassword(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (input.length() > 7) {
                return input;
            }
            System.out.println("ERRORE: Password troppo corta.");
        }
    }

    @SuppressWarnings("java:S106") // Soppresso SOLO qui
    public static void print(String message) {
        System.out.println(message);
    }

    @SuppressWarnings("java:S106") // Soppresso SOLO qui
    public static void printf(String message, Object object1, Object object2, Object object3, Object object4) {
        System.out.printf(message,  object1, object2, object3, object4);
    }

    @SuppressWarnings("java:S106") // Soppresso SOLO qui
    public static void printf2(String message, Object object1, Object object2, Object object3) {
        System.out.printf(message,  object1, object2, object3);
    }
}
