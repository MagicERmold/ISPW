package com.stocktrack.view.cli;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.RegistrazioneBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AcquistaProdottiFornitoriCLI {

    private final AcquistaProdottiFornitoriBoundary boundary = new AcquistaProdottiFornitoriBoundary();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("StockTrack - Acquista prodotti da fornitore");
        ProfiloUtenteBean profiloUtente = autentica();
        if (profiloUtente == null) {
            System.out.println("Accesso non riuscito");
            return;
        }

        System.out.println("Benvenuto " + profiloUtente.getNome() + " (" + profiloUtente.getRuolo() + ")");
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            System.out.println("Nessun fornitore selezionato");
            return;
        }

        List<ProdottoBean> prodottiDisponibili = boundary.recuperaProdotti(fornitore);
        if (prodottiDisponibili.isEmpty()) {
            System.out.println("Prodotti fornitore non disponibili");
            return;
        }

        List<ProdottoBean> prodottiSelezionati = selezionaProdotti(prodottiDisponibili);
        CarrelloBean carrello = boundary.configuraCarrello(prodottiSelezionati);
        if (carrello.getProdotti().isEmpty()) {
            System.out.println("Carrello non valido");
            return;
        }

        System.out.println("Totale stimato: " + carrello.getTotaleStimato() + " EUR");
        EsitoPagamentoBean esitoPagamento = boundary.effettuaPagamento(creaPagamento(carrello.getTotaleStimato()));
        System.out.println(esitoPagamento.getMessaggio());
        if (!esitoPagamento.isSuccesso()) {
            return;
        }

        OrdineBean ordineBean = new OrdineBean("ORD-" + UUID.randomUUID(), fornitore,
                carrello.getProdotti(), carrello.getTotaleStimato());
        EsitoOrdineBean esitoOrdine = boundary.confermaOrdine(ordineBean);
        System.out.println(esitoOrdine.getMessaggio());
    }

    private ProfiloUtenteBean autentica() {
        System.out.println();
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        int scelta = leggiIntero("Scegli: ", 1, 2);
        if (scelta == 2) {
            return registra();
        }
        return login();
    }

    private ProfiloUtenteBean login() {
        System.out.println();
        System.out.println("Login");
        System.out.print("Email utente: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        return boundary.login(new LoginBean(username, password));
    }

    private ProfiloUtenteBean registra() {
        System.out.println();
        System.out.println("Registrazione");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println("1. Titolare");
        System.out.println("2. Commesso");
        int sceltaRuolo = leggiIntero("Ruolo: ", 1, 2);
        RuoloUtente ruolo = sceltaRuolo == 1 ? RuoloUtente.TITOLARE : RuoloUtente.COMMESSO;
        return boundary.registra(new RegistrazioneBean(nome, cognome, email, password, ruolo));
    }

    private FornitoreBean scegliFornitore() {
        List<FornitoreBean> fornitori = boundary.recuperaFornitori();
        if (fornitori.isEmpty()) {
            return null;
        }

        System.out.println();
        System.out.println("Fornitori disponibili");
        for (int index = 0; index < fornitori.size(); index++) {
            FornitoreBean fornitore = fornitori.get(index);
            System.out.println((index + 1) + ". " + fornitore.getNome() + " - " + fornitore.getEmail());
        }

        int scelta = leggiIntero("Seleziona fornitore: ", 1, fornitori.size());
        return fornitori.get(scelta - 1);
    }

    private List<ProdottoBean> selezionaProdotti(List<ProdottoBean> prodottiDisponibili) {
        List<ProdottoBean> selezionati = new ArrayList<>();
        boolean continua = true;

        while (continua) {
            stampaProdotti(prodottiDisponibili);
            int scelta = leggiIntero("Seleziona prodotto: ", 1, prodottiDisponibili.size());
            ProdottoBean prodotto = prodottiDisponibili.get(scelta - 1);
            if (prodotto.getQuantita() <= 0) {
                System.out.println("Prodotto non disponibile");
                continue;
            }
            int quantita = leggiIntero("Quantita da acquistare: ", 1, Math.max(1, prodotto.getQuantita()));
            selezionati.add(new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                    quantita, prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario()));

            System.out.print("Aggiungere un altro prodotto? (s/n): ");
            continua = scanner.nextLine().trim().equalsIgnoreCase("s");
        }

        return selezionati;
    }

    private void stampaProdotti(List<ProdottoBean> prodotti) {
        System.out.println();
        System.out.println("Prodotti fornitore");
        for (int index = 0; index < prodotti.size(); index++) {
            ProdottoBean prodotto = prodotti.get(index);
            System.out.println((index + 1) + ". " + prodotto.getNome()
                    + " | disponibili: " + prodotto.getQuantita()
                    + " | prezzo: " + prodotto.getPrezzoUnitario());
        }
    }

    private PagamentoBean creaPagamento(BigDecimal importo) {
        System.out.println();
        System.out.println("Pagamento");
        System.out.println("1. Visa");
        System.out.println("2. PayPal");
        int scelta = leggiIntero("Metodo pagamento: ", 1, 2);

        if (scelta == 1) {
            System.out.print("Numero carta Visa: ");
            String numeroCarta = scanner.nextLine();
            System.out.print("CVV: ");
            String cvv = scanner.nextLine();
            return new PagamentoBean("VISA", numeroCarta, cvv, null, importo, "EUR");
        }

        System.out.print("Email account PayPal: ");
        String emailAccount = scanner.nextLine();
        return new PagamentoBean("PAYPAL", null, null, emailAccount, importo, "EUR");
    }

    private int leggiIntero(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Richiedi nuovamente il valore.
            }
            System.out.println("Valore non valido");
        }
    }
}
