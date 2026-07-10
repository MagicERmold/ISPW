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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AcquistaProdottiFornitoriCLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcquistaProdottiFornitoriCLI.class);

    private final AcquistaProdottiFornitoriBoundary boundary = new AcquistaProdottiFornitoriBoundary();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        LOGGER.info("StockTrack - Acquista prodotti da fornitore");
        ProfiloUtenteBean profiloUtente = autentica();
        if (profiloUtente == null) {
            LOGGER.info("Accesso non riuscito");
            return;
        }

        LOGGER.info("Benvenuto {} ({})", profiloUtente.getNome(), profiloUtente.getRuolo());
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            LOGGER.info("Nessun fornitore selezionato");
            return;
        }

        List<ProdottoBean> prodottiDisponibili = boundary.recuperaProdotti(fornitore);
        if (prodottiDisponibili.isEmpty()) {
            LOGGER.info("Prodotti fornitore non disponibili");
            return;
        }

        List<ProdottoBean> prodottiSelezionati = selezionaProdotti(prodottiDisponibili);
        CarrelloBean carrello = boundary.configuraCarrello(prodottiSelezionati);
        if (carrello.getProdotti().isEmpty()) {
            LOGGER.info("Carrello non valido");
            return;
        }

        LOGGER.info("Totale stimato: {} EUR", carrello.getTotaleStimato());
        EsitoPagamentoBean esitoPagamento = boundary.effettuaPagamento(creaPagamento(carrello.getTotaleStimato()));
        LOGGER.info(esitoPagamento.getMessaggio());
        if (!esitoPagamento.isSuccesso()) {
            return;
        }

        OrdineBean ordineBean = new OrdineBean("ORD-" + UUID.randomUUID(), fornitore,
                carrello.getProdotti(), carrello.getTotaleStimato());
        EsitoOrdineBean esitoOrdine = boundary.confermaOrdine(ordineBean);
        LOGGER.info(esitoOrdine.getMessaggio());
    }

    private ProfiloUtenteBean autentica() {
        LOGGER.info("");
        LOGGER.info("1. Login");
        LOGGER.info("2. Registrazione");
        int scelta = leggiIntero("Scegli: ", 1, 2);
        if (scelta == 2) {
            return registra();
        }
        return login();
    }

    private ProfiloUtenteBean login() {
        LOGGER.info("");
        LOGGER.info("Login");
        LOGGER.info("Email utente:");
        String username = scanner.nextLine();
        LOGGER.info("Password:");
        String password = scanner.nextLine();
        return boundary.login(new LoginBean(username, password));
    }

    private ProfiloUtenteBean registra() {
        LOGGER.info("");
        LOGGER.info("Registrazione");
        LOGGER.info("Nome:");
        String nome = scanner.nextLine();
        LOGGER.info("Cognome:");
        String cognome = scanner.nextLine();
        LOGGER.info("Email:");
        String email = scanner.nextLine();
        LOGGER.info("Password:");
        String password = scanner.nextLine();
        LOGGER.info("1. Titolare");
        LOGGER.info("2. Commesso");
        int sceltaRuolo = leggiIntero("Ruolo: ", 1, 2);
        RuoloUtente ruolo = sceltaRuolo == 1 ? RuoloUtente.TITOLARE : RuoloUtente.COMMESSO;
        return boundary.registra(new RegistrazioneBean(nome, cognome, email, password, ruolo));
    }

    private FornitoreBean scegliFornitore() {
        List<FornitoreBean> fornitori = boundary.recuperaFornitori();
        if (fornitori.isEmpty()) {
            return null;
        }

        LOGGER.info("");
        LOGGER.info("Fornitori disponibili");
        for (int index = 0; index < fornitori.size(); index++) {
            FornitoreBean fornitore = fornitori.get(index);
            LOGGER.info("{}. {} - {}", index + 1, fornitore.getNome(), fornitore.getEmail());
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
                LOGGER.info("Prodotto non disponibile");
                continue;
            }
            int quantita = leggiIntero("Quantita da acquistare: ", 1, Math.max(1, prodotto.getQuantita()));
            selezionati.add(new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                    quantita, prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario()));

            LOGGER.info("Aggiungere un altro prodotto? (s/n):");
            continua = scanner.nextLine().trim().equalsIgnoreCase("s");
        }

        return selezionati;
    }

    private void stampaProdotti(List<ProdottoBean> prodotti) {
        LOGGER.info("");
        LOGGER.info("Prodotti fornitore");
        for (int index = 0; index < prodotti.size(); index++) {
            ProdottoBean prodotto = prodotti.get(index);
            LOGGER.info("{}. {} | disponibili: {} | prezzo: {}", index + 1, prodotto.getNome(),
                    prodotto.getQuantita(), prodotto.getPrezzoUnitario());
        }
    }

    private PagamentoBean creaPagamento(BigDecimal importo) {
        LOGGER.info("");
        LOGGER.info("Pagamento");
        LOGGER.info("1. Visa");
        LOGGER.info("2. PayPal");
        int scelta = leggiIntero("Metodo pagamento: ", 1, 2);

        if (scelta == 1) {
            LOGGER.info("Numero carta Visa:");
            String numeroCarta = scanner.nextLine();
            LOGGER.info("CVV:");
            String cvv = scanner.nextLine();
            return new PagamentoBean("VISA", numeroCarta, cvv, null, importo, "EUR");
        }

        LOGGER.info("Email account PayPal:");
        String emailAccount = scanner.nextLine();
        return new PagamentoBean("PAYPAL", null, null, emailAccount, importo, "EUR");
    }

    private int leggiIntero(String prompt, int min, int max) {
        while (true) {
            LOGGER.info(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Richiedi nuovamente il valore.
            }
            LOGGER.info("Valore non valido");
        }
    }
}
