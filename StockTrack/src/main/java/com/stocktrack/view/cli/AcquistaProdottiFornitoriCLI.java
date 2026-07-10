package com.stocktrack.view.cli;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.EsitoOperazioneBean;
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
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import com.stocktrack.boundary.AnalizzaDisponibilitaInventarioBoundary;
import com.stocktrack.boundary.GestisciFornitoriBoundary;
import com.stocktrack.boundary.GestisciInventarioFornitoreBoundary;
import com.stocktrack.boundary.GestisciProdottiBoundary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AcquistaProdottiFornitoriCLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcquistaProdottiFornitoriCLI.class);
    private static final String BACK_OPTION = "0. Indietro";

    private final AcquistaProdottiFornitoriBoundary acquistoBoundary = new AcquistaProdottiFornitoriBoundary();
    private final AnalizzaDisponibilitaInventarioBoundary inventarioBoundary =
            new AnalizzaDisponibilitaInventarioBoundary();
    private final GestisciProdottiBoundary prodottiBoundary = new GestisciProdottiBoundary();
    private final GestisciFornitoriBoundary fornitoriBoundary = new GestisciFornitoriBoundary();
    private final GestisciInventarioFornitoreBoundary inventarioFornitoreBoundary =
            new GestisciInventarioFornitoreBoundary();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        LOGGER.info("StockTrack CLI");
        while (true) {
            ProfiloUtenteBean profiloUtente = autentica();
            if (profiloUtente == null) {
                return;
            }

            LOGGER.info("Benvenuto {} ({})", profiloUtente.getNome(), profiloUtente.getRuolo());
            switch (profiloUtente.getRuolo()) {
                case TITOLARE -> menuTitolare();
                case COMMESSO -> menuCommesso();
                case FORNITORE -> menuFornitore();
            }
        }
    }

    private ProfiloUtenteBean autentica() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("1. Login");
            LOGGER.info("2. Registrazione");
            LOGGER.info("0. Esci");
            int scelta = leggiIntero("Scegli: ", 0, 2);
            if (scelta == 0) {
                return null;
            }

            ProfiloUtenteBean profiloUtente = scelta == 2 ? registra() : login();
            if (profiloUtente != null) {
                return profiloUtente;
            }

            LOGGER.info("Accesso non riuscito");
            continua = conferma("Vuoi riprovare? (s/n): ");
        }
        return null;
    }

    private ProfiloUtenteBean login() {
        LOGGER.info("");
        LOGGER.info("Login");
        String username = leggiTesto("Email utente: ");
        String password = leggiTesto("Password: ");
        return acquistoBoundary.login(new LoginBean(username, password));
    }

    private ProfiloUtenteBean registra() {
        LOGGER.info("");
        LOGGER.info("Registrazione");
        String nome = leggiTesto("Nome: ");
        String cognome = leggiTesto("Cognome: ");
        String email = leggiTesto("Email: ");
        String password = leggiTesto("Password: ");
        LOGGER.info("1. Titolare");
        LOGGER.info("2. Commesso");
        LOGGER.info("3. Fornitore");
        int sceltaRuolo = leggiIntero("Ruolo: ", 1, 3);
        RuoloUtente ruolo = switch (sceltaRuolo) {
            case 1 -> RuoloUtente.TITOLARE;
            case 2 -> RuoloUtente.COMMESSO;
            default -> RuoloUtente.FORNITORE;
        };
        return acquistoBoundary.registra(new RegistrazioneBean(nome, cognome, email, password, ruolo));
    }

    private void menuTitolare() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Menu titolare");
            LOGGER.info("1. Visualizza inventario Euronics");
            LOGGER.info("2. Gestisci prodotti");
            LOGGER.info("3. Acquista da fornitori");
            LOGGER.info("4. Gestisci fornitori");
            LOGGER.info("5. Statistiche vendite/acquisti");
            LOGGER.info("0. Logout");
            int scelta = leggiIntero("Scegli: ", 0, 5);
            switch (scelta) {
                case 1 -> visualizzaInventario();
                case 2 -> menuGestisciProdotti();
                case 3 -> acquistaDaFornitore();
                case 4 -> menuGestisciFornitori();
                case 5 -> visualizzaStatistiche();
                default -> continua = false;
            }
        }
        acquistoBoundary.logout();
    }

    private void menuCommesso() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Menu commesso");
            LOGGER.info("1. Visualizza inventario Euronics");
            LOGGER.info("2. Gestisci prodotti");
            LOGGER.info("3. Statistiche vendite/acquisti");
            LOGGER.info("0. Logout");
            int scelta = leggiIntero("Scegli: ", 0, 3);
            switch (scelta) {
                case 1 -> visualizzaInventario();
                case 2 -> menuGestisciProdotti();
                case 3 -> visualizzaStatistiche();
                default -> continua = false;
            }
        }
        acquistoBoundary.logout();
    }

    private void menuFornitore() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Menu fornitore");
            LOGGER.info("1. Visualizza il mio magazzino");
            LOGGER.info("2. Aggiungi o modifica prodotto");
            LOGGER.info("0. Logout");
            int scelta = leggiIntero("Scegli: ", 0, 2);
            switch (scelta) {
                case 1 -> visualizzaInventarioFornitore();
                case 2 -> salvaProdottoFornitore();
                default -> continua = false;
            }
        }
        inventarioFornitoreBoundary.logout();
    }

    private void visualizzaInventario() {
        List<DisponibilitaProdottoBean> disponibilita = inventarioBoundary.analizzaDisponibilita();
        if (disponibilita.isEmpty()) {
            LOGGER.info("Inventario vuoto");
            return;
        }
        disponibilita.forEach(item -> LOGGER.info("{} | qta {} | {}",
                item.getProdotto().getNome(), item.getQuantitaDisponibile(), item.getMessaggio()));
    }

    private void menuGestisciProdotti() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Gestisci prodotti");
            LOGGER.info("1. Lista prodotti");
            LOGGER.info("2. Aggiungi prodotto");
            LOGGER.info("3. Modifica prodotto");
            LOGGER.info("4. Rimuovi prodotto");
            LOGGER.info("5. Registra vendita manuale");
            LOGGER.info("6. Registra acquisto esterno");
            LOGGER.info(BACK_OPTION);
            int scelta = leggiIntero("Scegli: ", 0, 6);
            switch (scelta) {
                case 1 -> stampaProdotti(prodottiBoundary.visualizzaProdotti());
                case 2 -> stampaEsito(prodottiBoundary.aggiungiProdotto(leggiProdotto()));
                case 3 -> stampaEsito(prodottiBoundary.modificaProdotto(leggiProdotto()));
                case 4 -> rimuoviProdotto();
                case 5 -> registraMovimentoManuale(true);
                case 6 -> registraMovimentoManuale(false);
                default -> continua = false;
            }
        }
    }

    private void rimuoviProdotto() {
        String id = leggiTesto("Id prodotto da rimuovere: ");
        stampaEsito(prodottiBoundary.rimuoviProdotto(new ProdottoBean(id, "placeholder", "placeholder", 0, 0,
                BigDecimal.ZERO)));
    }

    private void registraMovimentoManuale(boolean vendita) {
        String id = leggiTesto("Id prodotto: ");
        int quantita = leggiIntero("Quantita: ", 1, Integer.MAX_VALUE);
        EsitoOperazioneBean esito = vendita
                ? prodottiBoundary.registraVenditaManuale(id, quantita)
                : prodottiBoundary.registraAcquistoEsterno(id, quantita);
        stampaEsito(esito);
    }

    private void acquistaDaFornitore() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            LOGGER.info("Nessun fornitore disponibile");
            return;
        }

        List<ProdottoBean> prodottiDisponibili = acquistoBoundary.recuperaProdotti(fornitore);
        if (prodottiDisponibili.isEmpty()) {
            LOGGER.info("Prodotti fornitore non disponibili");
            return;
        }

        List<ProdottoBean> prodottiSelezionati = selezionaProdotti(prodottiDisponibili);
        CarrelloBean carrello = acquistoBoundary.configuraCarrello(prodottiSelezionati);
        if (carrello.getProdotti().isEmpty()) {
            LOGGER.info("Carrello non valido");
            return;
        }

        LOGGER.info("Totale stimato: {} EUR", carrello.getTotaleStimato());
        EsitoPagamentoBean esitoPagamento = acquistoBoundary.effettuaPagamento(creaPagamento(carrello.getTotaleStimato()));
        LOGGER.info(esitoPagamento.getMessaggio());
        if (!esitoPagamento.isSuccesso()) {
            return;
        }

        OrdineBean ordineBean = new OrdineBean("ORD-" + UUID.randomUUID(), fornitore,
                carrello.getProdotti(), carrello.getTotaleStimato());
        EsitoOrdineBean esitoOrdine = acquistoBoundary.confermaOrdine(ordineBean);
        LOGGER.info(esitoOrdine.getMessaggio());
    }

    private void menuGestisciFornitori() {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Gestisci fornitori");
            LOGGER.info("1. Lista fornitori");
            LOGGER.info("2. Aggiungi fornitore con codice");
            LOGGER.info("3. Rimuovi fornitore");
            LOGGER.info("4. Visualizza inventario fornitore");
            LOGGER.info(BACK_OPTION);
            int scelta = leggiIntero("Scegli: ", 0, 4);
            switch (scelta) {
                case 1 -> stampaFornitori(fornitoriBoundary.visualizzaFornitori());
                case 2 -> aggiungiFornitore();
                case 3 -> rimuoviFornitore();
                case 4 -> visualizzaInventarioFornitoreDaTitolare();
                default -> continua = false;
            }
        }
    }

    private void aggiungiFornitore() {
        String codice = leggiTesto("Codice fornitore: ");
        stampaEsito(fornitoriBoundary.aggiungiFornitoreConCodice(codice));
    }

    private void rimuoviFornitore() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            LOGGER.info("Nessun fornitore selezionato");
            return;
        }
        stampaEsito(fornitoriBoundary.rimuoviFornitore(fornitore));
    }

    private void visualizzaInventarioFornitoreDaTitolare() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            LOGGER.info("Nessun fornitore selezionato");
            return;
        }
        stampaProdotti(fornitoriBoundary.visualizzaInventarioFornitore(fornitore));
    }

    private void visualizzaStatistiche() {
        List<StatisticaVenditaMensileBean> statistiche = prodottiBoundary.analizzaStatisticheVenditaMensili();
        if (statistiche.isEmpty()) {
            LOGGER.info("Statistiche non disponibili");
            return;
        }
        statistiche.forEach(statistica -> LOGGER.info("{} | venduti {} | acquistati {} | incasso {} EUR | acquisti {} EUR | top {}",
                statistica.getMese(), statistica.getQuantitaVenduta(), statistica.getQuantitaAcquistata(),
                statistica.getIncassoStimato(), statistica.getSpesaAcquisti(), statistica.getProdottoPiuVenduto()));
    }

    private void visualizzaInventarioFornitore() {
        FornitoreBean profilo = inventarioFornitoreBoundary.visualizzaProfilo();
        LOGGER.info("Magazzino {}", profilo.getNome());
        stampaProdotti(inventarioFornitoreBoundary.visualizzaInventario());
    }

    private void salvaProdottoFornitore() {
        stampaEsito(inventarioFornitoreBoundary.salvaProdotto(leggiProdottoFornitore()));
    }

    private FornitoreBean scegliFornitore() {
        List<FornitoreBean> fornitori = fornitoriBoundary.visualizzaFornitori();
        if (fornitori.isEmpty()) {
            return null;
        }

        stampaFornitori(fornitori);
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

            continua = conferma("Aggiungere un altro prodotto? (s/n): ");
        }

        return selezionati;
    }

    private ProdottoBean leggiProdotto() {
        String id = leggiTesto("Id: ");
        String nome = leggiTesto("Nome: ");
        String categoria = leggiTesto("Categoria: ");
        int quantita = leggiIntero("Quantita: ", 0, Integer.MAX_VALUE);
        int soglia = leggiIntero("Soglia minima: ", 0, Integer.MAX_VALUE);
        BigDecimal prezzo = leggiDecimal("Prezzo unitario: ");
        return new ProdottoBean(id, nome, categoria, quantita, soglia, prezzo);
    }

    private ProdottoBean leggiProdottoFornitore() {
        String id = leggiTesto("Id prodotto fornitore: ");
        String nome = leggiTesto("Nome: ");
        String categoria = leggiTesto("Categoria: ");
        int quantita = leggiIntero("Scorte disponibili: ", 0, Integer.MAX_VALUE);
        BigDecimal prezzo = leggiDecimal("Prezzo unitario: ");
        return new ProdottoBean(id, nome, categoria, quantita, 0, prezzo);
    }

    private void stampaProdotti(List<ProdottoBean> prodotti) {
        if (prodotti.isEmpty()) {
            LOGGER.info("Nessun prodotto");
            return;
        }
        for (int index = 0; index < prodotti.size(); index++) {
            ProdottoBean prodotto = prodotti.get(index);
            LOGGER.info("{}. {} | {} | qta {} | prezzo {}", index + 1, prodotto.getId(),
                    prodotto.getNome(), prodotto.getQuantita(), prodotto.getPrezzoUnitario());
        }
    }

    private void stampaFornitori(List<FornitoreBean> fornitori) {
        if (fornitori.isEmpty()) {
            LOGGER.info("Nessun fornitore");
            return;
        }
        for (int index = 0; index < fornitori.size(); index++) {
            FornitoreBean fornitore = fornitori.get(index);
            LOGGER.info("{}. {} | codice {} | {}", index + 1, fornitore.getNome(), fornitore.getId(),
                    fornitore.getEmail());
        }
    }

    private PagamentoBean creaPagamento(BigDecimal importo) {
        LOGGER.info("");
        LOGGER.info("Pagamento");
        LOGGER.info("1. Visa");
        LOGGER.info("2. PayPal");
        int scelta = leggiIntero("Metodo pagamento: ", 1, 2);

        if (scelta == 1) {
            String numeroCarta = leggiTesto("Numero carta Visa: ");
            String cvv = leggiTesto("CVV: ");
            return new PagamentoBean("VISA", numeroCarta, cvv, null, importo, "EUR");
        }

        String emailAccount = leggiTesto("Email account PayPal: ");
        return new PagamentoBean("PAYPAL", null, null, emailAccount, importo, "EUR");
    }

    private void stampaEsito(EsitoOperazioneBean esito) {
        LOGGER.info(esito.getMessaggio());
    }

    private String leggiTesto(String prompt) {
        LOGGER.info(prompt);
        return scanner.nextLine();
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

    private BigDecimal leggiDecimal(String prompt) {
        while (true) {
            LOGGER.info(prompt);
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                LOGGER.info("Valore non valido");
            }
        }
    }

    private boolean conferma(String prompt) {
        LOGGER.info(prompt);
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }
}
