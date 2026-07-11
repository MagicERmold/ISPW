package com.stocktrack.view.cli;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProdottoSelezionatoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.QuantitaProdottoBean;
import com.stocktrack.bean.RegistrazioneBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.boundary.AcquistaProdottiFornitoriBoundary;
import com.stocktrack.boundary.AnalizzaDisponibilitaInventarioBoundary;
import com.stocktrack.boundary.GestisciFornitoriBoundary;
import com.stocktrack.boundary.GestisciInventarioFornitoreBoundary;
import com.stocktrack.boundary.GestisciProdottiBoundary;
import com.stocktrack.view.support.ProductImageAssetStore;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AcquistaProdottiFornitoriCLI {

    private static final ConsoleOutput LOGGER = new ConsoleOutput();
    private static final String BACK_OPTION = "0. Indietro";
    private static final String LOGOUT_OPTION = "0. Logout";
    private static final String CHOOSE_PROMPT = "Scegli: ";
    private static final String INVENTORY_OPTION = "1. Visualizza inventario Euronics";
    private static final String INVALID_VALUE = "Valore non valido";
    private static final String CURRENCY_CODE = "EUR";
    private static final String CURRENCY_EUR = " EUR";

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
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 2);
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
            LOGGER.info(INVENTORY_OPTION);
            LOGGER.info("2. Acquista da fornitori");
            LOGGER.info("3. Gestisci fornitori");
            LOGGER.info("4. Statistiche vendite/acquisti");
            LOGGER.info(LOGOUT_OPTION);
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 4);
            switch (scelta) {
                case 1 -> visualizzaInventario();
                case 2 -> acquistaDaFornitore();
                case 3 -> menuGestisciFornitori();
                case 4 -> visualizzaStatistiche();
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
            LOGGER.info(INVENTORY_OPTION);
            LOGGER.info("2. Statistiche vendite/acquisti");
            LOGGER.info(LOGOUT_OPTION);
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 2);
            switch (scelta) {
                case 1 -> visualizzaInventario();
                case 2 -> visualizzaStatistiche();
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
            LOGGER.info(LOGOUT_OPTION);
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 2);
            switch (scelta) {
                case 1 -> visualizzaInventarioFornitore();
                case 2 -> salvaProdottoFornitore();
                default -> continua = false;
            }
        }
        inventarioFornitoreBoundary.logout();
    }

    private void visualizzaInventario() {
        EsitoListaBean<DisponibilitaProdottoBean> esito = inventarioBoundary.analizzaDisponibilitaConEsito();
        List<DisponibilitaProdottoBean> disponibilita = esito.getElementi();
        if (disponibilita.isEmpty()) {
            LOGGER.info(esito.getMessaggio());
            return;
        }
        disponibilita.forEach(item -> LOGGER.info("{} | qta {} | {}",
                item.getProdotto().getNome(), item.getQuantitaDisponibile(), item.getMessaggio()));
        if (conferma("Gestire un prodotto dell'inventario? (s/n): ")) {
            menuGestisciProdotto(leggiTesto("Id prodotto: "));
        }
    }

    private void menuGestisciProdotto(String idProdotto) {
        boolean continua = true;
        while (continua) {
            LOGGER.info("");
            LOGGER.info("Prodotto {}", idProdotto);
            LOGGER.info("1. Modifica quantita prodotto");
            LOGGER.info("2. Rimuovi prodotto");
            LOGGER.info("3. Registra vendita manuale");
            LOGGER.info("4. Registra acquisto esterno");
            LOGGER.info(BACK_OPTION);
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 4);
            switch (scelta) {
                case 1 -> modificaQuantitaProdotto(idProdotto);
                case 2 -> rimuoviProdotto(idProdotto);
                case 3 -> registraMovimentoManuale(idProdotto, true);
                case 4 -> registraMovimentoManuale(idProdotto, false);
                default -> continua = false;
            }
        }
    }

    private void modificaQuantitaProdotto(String idProdotto) {
        int quantita = leggiIntero("Nuova quantita: ", 0, Integer.MAX_VALUE);
        stampaEsito(prodottiBoundary.modificaQuantitaProdotto(new QuantitaProdottoBean(idProdotto, quantita)));
    }

    private void rimuoviProdotto(String idProdotto) {
        stampaEsito(prodottiBoundary.rimuoviProdotto(new ProdottoSelezionatoBean(idProdotto)));
    }

    private void registraMovimentoManuale(String idProdotto, boolean vendita) {
        int quantita = leggiIntero("Quantita: ", 1, Integer.MAX_VALUE);
        QuantitaProdottoBean movimentoProdottoBean = new QuantitaProdottoBean(idProdotto, quantita);
        EsitoOperazioneBean esito = vendita
                ? prodottiBoundary.registraVenditaManuale(movimentoProdottoBean)
                : prodottiBoundary.registraAcquistoEsterno(movimentoProdottoBean);
        stampaEsito(esito);
    }

    private void acquistaDaFornitore() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            return;
        }

        EsitoListaBean<ProdottoBean> esitoProdotti = acquistoBoundary.recuperaProdottiConEsito(fornitore);
        List<ProdottoBean> prodottiDisponibili = esitoProdotti.getElementi();
        if (prodottiDisponibili.isEmpty()) {
            LOGGER.info(esitoProdotti.getMessaggio());
            return;
        }

        List<ProdottoBean> prodottiSelezionati = selezionaProdotti(prodottiDisponibili);
        CarrelloBean carrello = acquistoBoundary.configuraCarrello(prodottiSelezionati);
        if (carrello.getProdotti().isEmpty()) {
            LOGGER.info(carrello.getMessaggio());
            return;
        }

        LOGGER.info("Totale stimato: {}", carrello.getTotaleStimato() + CURRENCY_EUR);
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
            int scelta = leggiIntero(CHOOSE_PROMPT, 0, 4);
            switch (scelta) {
                case 1 -> visualizzaFornitori();
                case 2 -> aggiungiFornitore();
                case 3 -> rimuoviFornitore();
                case 4 -> visualizzaInventarioFornitoreDaTitolare();
                default -> continua = false;
            }
        }
    }

    private void visualizzaFornitori() {
        EsitoListaBean<FornitoreBean> esito = fornitoriBoundary.visualizzaFornitoriConEsito();
        stampaFornitori(esito.getElementi(), esito.getMessaggio());
    }

    private void aggiungiFornitore() {
        String codice = leggiTesto("Codice fornitore: ");
        stampaEsito(fornitoriBoundary.aggiungiFornitoreConCodice(codice));
    }

    private void rimuoviFornitore() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            return;
        }
        stampaEsito(fornitoriBoundary.rimuoviFornitore(fornitore));
    }

    private void visualizzaInventarioFornitoreDaTitolare() {
        FornitoreBean fornitore = scegliFornitore();
        if (fornitore == null) {
            return;
        }
        EsitoListaBean<ProdottoBean> esito = fornitoriBoundary.visualizzaInventarioFornitoreConEsito(fornitore);
        stampaProdotti(esito.getElementi(), esito.getMessaggio());
    }

    private void visualizzaStatistiche() {
        EsitoListaBean<StatisticaVenditaMensileBean> esito =
                prodottiBoundary.analizzaStatisticheVenditaMensiliConEsito();
        List<StatisticaVenditaMensileBean> statistiche = esito.getElementi();
        if (statistiche.isEmpty()) {
            LOGGER.info(esito.getMessaggio());
            return;
        }
        statistiche.forEach(statistica -> LOGGER.info("{} | venduti {} | acquistati {} | incasso {} | acquisti {} | top {}",
                statistica.getMese(), statistica.getQuantitaVenduta(), statistica.getQuantitaAcquistata(),
                statistica.getIncassoStimato() + CURRENCY_EUR, statistica.getSpesaAcquisti() + CURRENCY_EUR,
                statistica.getProdottoPiuVenduto()));
    }

    private void visualizzaInventarioFornitore() {
        FornitoreBean profilo = inventarioFornitoreBoundary.visualizzaProfilo();
        LOGGER.info("Magazzino {}", profilo.getNome());
        EsitoListaBean<ProdottoBean> esito = inventarioFornitoreBoundary.visualizzaInventarioConEsito();
        stampaProdotti(esito.getElementi(), esito.getMessaggio());
    }

    private void salvaProdottoFornitore() {
        ProdottoBean prodotto = leggiProdottoFornitore();
        EsitoOperazioneBean esito = inventarioFornitoreBoundary.salvaProdotto(prodotto);
        stampaEsito(esito);
        if (esito.isSuccesso()) {
            salvaFotoProdottoFornitore(prodotto);
        }
    }

    private void salvaFotoProdottoFornitore(ProdottoBean prodotto) {
        String percorsoFoto = leggiTesto("Percorso foto prodotto (invio per saltare): ");
        if (percorsoFoto.isBlank()) {
            return;
        }
        try {
            ProductImageAssetStore.saveProductImage(Path.of(percorsoFoto), prodotto);
            LOGGER.info("Foto prodotto salvata");
        } catch (IOException e) {
            LOGGER.info("Foto non salvata: {}", e.getMessage());
        }
    }

    private FornitoreBean scegliFornitore() {
        EsitoListaBean<FornitoreBean> esito = fornitoriBoundary.visualizzaFornitoriConEsito();
        List<FornitoreBean> fornitori = esito.getElementi();
        if (fornitori.isEmpty()) {
            LOGGER.info(esito.getMessaggio());
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
            } else {
                int quantita = leggiIntero("Quantita da acquistare: ", 1, Math.max(1, prodotto.getQuantita()));
                selezionati.add(new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                        quantita, prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario()));
                continua = conferma("Aggiungere un altro prodotto? (s/n): ");
            }
        }

        return selezionati;
    }

    private ProdottoBean leggiProdottoFornitore() {
        String id = leggiTesto("Id prodotto fornitore: ");
        String nome = leggiTesto("Nome: ");
        String categoria = leggiTesto("Categoria: ");
        int quantita = leggiIntero("Scorte disponibili: ", 0, Integer.MAX_VALUE);
        BigDecimal prezzo = leggiDecimal();
        return new ProdottoBean(id, nome, categoria, quantita, 0, prezzo);
    }

    private void stampaProdotti(List<ProdottoBean> prodotti) {
        stampaProdotti(prodotti, "Nessun prodotto");
    }

    private void stampaProdotti(List<ProdottoBean> prodotti, String messaggioVuoto) {
        if (prodotti.isEmpty()) {
            LOGGER.info(messaggioVuoto);
            return;
        }
        for (int index = 0; index < prodotti.size(); index++) {
            ProdottoBean prodotto = prodotti.get(index);
            LOGGER.info("{}. {} | {} | qta {} | prezzo {}", index + 1, prodotto.getId(),
                    prodotto.getNome(), prodotto.getQuantita(), prodotto.getPrezzoUnitario());
        }
    }

    private void stampaFornitori(List<FornitoreBean> fornitori) {
        stampaFornitori(fornitori, "Nessun fornitore");
    }

    private void stampaFornitori(List<FornitoreBean> fornitori, String messaggioVuoto) {
        if (fornitori.isEmpty()) {
            LOGGER.info(messaggioVuoto);
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
            return new PagamentoBean("VISA", numeroCarta, cvv, null, importo, CURRENCY_CODE);
        }

        String emailAccount = leggiTesto("Email account PayPal: ");
        return new PagamentoBean("PAYPAL", null, null, emailAccount, importo, CURRENCY_CODE);
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
            LOGGER.info(INVALID_VALUE);
        }
    }

    private BigDecimal leggiDecimal() {
        while (true) {
            LOGGER.info("Prezzo unitario: ");
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                LOGGER.info(INVALID_VALUE);
            }
        }
    }

    private boolean conferma(String prompt) {
        LOGGER.info(prompt);
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    private static final class ConsoleOutput {

        private static final PrintWriter WRITER = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(FileDescriptor.out), StandardCharsets.UTF_8), true);

        private void info(String message) {
            WRITER.println(message);
        }

        private void info(String template, Object... values) {
            WRITER.println(format(template, values));
        }

        private String format(String template, Object... values) {
            StringBuilder builder = new StringBuilder();
            int start = 0;
            for (Object value : values) {
                int placeholder = template.indexOf("{}", start);
                if (placeholder < 0) {
                    break;
                }
                builder.append(template, start, placeholder);
                builder.append(value);
                start = placeholder + 2;
            }
            builder.append(template.substring(start));
            return builder.toString();
        }
    }
}
