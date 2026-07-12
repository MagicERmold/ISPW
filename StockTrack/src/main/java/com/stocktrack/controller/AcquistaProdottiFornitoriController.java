package com.stocktrack.controller;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PagamentoFallitoException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.exceptions.ProdottoNonDisponibileException;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.entity.Ordine;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.TipoMovimentoInventario;
import com.stocktrack.pattern.adapter.FornitoreApiAdapter;
import com.stocktrack.pattern.adapter.FornitoreGateway;
import com.stocktrack.pattern.adapter.PagamentoGateway;
import com.stocktrack.pattern.adapter.PagamentoPaypalAdapter;
import com.stocktrack.pattern.adapter.PagamentoVisaAdapter;
import com.stocktrack.pattern.factory.DAOFactory;
import com.stocktrack.pattern.factory.DAOFactoryProvider;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller applicativo del caso d'uso principale di acquisto. Coordina bean, entity, DAO e gateway di pagamento e fornitore, applicando autorizzazioni e regole di business senza dipendere dalla view.
 */
public class AcquistaProdottiFornitoriController {

    private static final Object INVENTORY_UPDATE_LOCK = new Object();

    public List<FornitoreBean> recuperaFornitori() throws PersistenceException {
        return DAOFactoryProvider.getFactory().getFornitoreDAO().findAll().stream()
                .map(this::toFornitoreBean)
                .toList();
    }

    public List<ProdottoBean> recuperaProdotti(FornitoreBean fornitoreBean)
            throws FornitoreConnectionException {
        FornitoreGateway fornitoreGateway = new FornitoreApiAdapter();
        return fornitoreGateway.recuperaProdotti(fornitoreBean);
    }

    public CarrelloBean configuraCarrello(List<ProdottoBean> prodottiSelezionati)
            throws InvalidInputException, ProdottoNonDisponibileException {
        if (!isTitolare()) {
            throw new InvalidInputException("Solo il titolare puo acquistare prodotti dai fornitori");
        }
        if (prodottiSelezionati == null || prodottiSelezionati.isEmpty()) {
            throw new InvalidInputException("Selezionare almeno un prodotto");
        }

        BigDecimal totale = BigDecimal.ZERO;
        List<ProdottoBean> prodottiValidi = new ArrayList<>();
        for (ProdottoBean prodottoBean : prodottiSelezionati) {
            if (prodottoBean.getQuantita() <= 0) {
                throw new ProdottoNonDisponibileException("Prodotto non disponibile: " + prodottoBean.getNome());
            }
            prodottiValidi.add(prodottoBean);
            if (prodottoBean.getPrezzoUnitario() != null) {
                totale = totale.add(prodottoBean.getPrezzoUnitario()
                        .multiply(BigDecimal.valueOf(prodottoBean.getQuantita())));
            }
        }
        return new CarrelloBean(prodottiValidi, totale);
    }

    public EsitoPagamentoBean elaboraPagamento(PagamentoBean pagamentoBean) {
        if (!isTitolare()) {
            return new EsitoPagamentoBean(false, "Solo il titolare puo acquistare prodotti dai fornitori");
        }
        try {
            PagamentoGateway pagamentoGateway = selezionaPagamentoGateway(pagamentoBean.getMetodoPagamento());
            return pagamentoGateway.autorizzaPagamento(pagamentoBean);
        } catch (PagamentoFallitoException | IllegalArgumentException e) {
            return new EsitoPagamentoBean(false, e.getMessage());
        }
    }

    public EsitoOrdineBean acquistaProdottiDaFornitore(OrdineBean ordineBean) {
        if (!isTitolare()) {
            return new EsitoOrdineBean(false,
                    "Solo il titolare puo acquistare prodotti dai fornitori");
        }
        FornitoreGateway fornitoreGateway = new FornitoreApiAdapter();
        try {
            fornitoreGateway.notificaOrdine(ordineBean);
            DAOFactory daoFactory = DAOFactoryProvider.getFactory();
            aggiornaInventario(daoFactory, ordineBean);
            registraMovimentiAcquisto(daoFactory, ordineBean);
            Ordine ordine = toOrdine(ordineBean);
            ordine.marcaPagato();
            daoFactory.getOrdineDAO().save(ordine);
            return new EsitoOrdineBean(true, "Ordine confermato, inventario aggiornato");
        } catch (FornitoreConnectionException | PersistenceException e) {
            return new EsitoOrdineBean(false, e.getMessage());
        }
    }

    private PagamentoGateway selezionaPagamentoGateway(String metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Metodo di pagamento obbligatorio");
        }

        return switch (metodoPagamento.trim().toUpperCase(Locale.ROOT)) {
            case "VISA" -> new PagamentoVisaAdapter();
            case "PAYPAL" -> new PagamentoPaypalAdapter();
            default -> throw new IllegalArgumentException("Metodo di pagamento non supportato");
        };
    }

    private boolean isTitolare() {
        return SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> RuoloUtente.TITOLARE.equals(session.getRuolo()))
                .orElse(false);
    }

    private void aggiornaInventario(DAOFactory daoFactory, OrdineBean ordineBean) throws PersistenceException {
        synchronized (INVENTORY_UPDATE_LOCK) {
            Inventario inventario = daoFactory.getInventarioDAO().findInventario();
            for (ProdottoBean prodottoBean : ordineBean.getProdotti()) {
                cercaProdottoDaAggiornare(inventario, prodottoBean)
                        .ifPresentOrElse(
                                prodotto -> prodotto.aumentaQuantita(prodottoBean.getQuantita()),
                                () -> inventario.aggiungiProdotto(toProdotto(prodottoBean))
                        );
            }
            daoFactory.getInventarioDAO().update(inventario);
        }
    }

    private Optional<Prodotto> cercaProdottoDaAggiornare(Inventario inventario, ProdottoBean prodottoBean) {
        Optional<Prodotto> prodottoPerId = inventario.cercaProdotto(prodottoBean.getId());
        if (prodottoPerId.isPresent()) {
            return prodottoPerId;
        }
        return inventario.getProdotti().stream()
                .filter(prodotto -> stessoProdottoCommerciale(prodotto, prodottoBean))
                .findFirst();
    }

    private boolean stessoProdottoCommerciale(Prodotto prodotto, ProdottoBean prodottoBean) {
        return normalizza(prodotto.getNome()).equals(normalizza(prodottoBean.getNome()))
                && normalizza(prodotto.getCategoria()).equals(normalizza(prodottoBean.getCategoria()));
    }

    private String normalizza(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private void registraMovimentiAcquisto(DAOFactory daoFactory, OrdineBean ordineBean) throws PersistenceException {
        String origine = "Fornitore " + ordineBean.getFornitore().getNome();
        for (ProdottoBean prodottoBean : ordineBean.getProdotti()) {
            BigDecimal valoreUnitario = prodottoBean.getPrezzoUnitario() == null ? BigDecimal.ZERO
                    : prodottoBean.getPrezzoUnitario();
            MovimentoInventario movimento = new MovimentoInventario("MOV-" + UUID.randomUUID(),
                    prodottoBean.getId(), prodottoBean.getNome(), TipoMovimentoInventario.ACQUISTO_FORNITORE,
                    prodottoBean.getQuantita(), valoreUnitario, origine);
            daoFactory.getMovimentoInventarioDAO().save(movimento);
        }
    }

    private FornitoreBean toFornitoreBean(Fornitore fornitore) {
        return new FornitoreBean(fornitore.getId(), fornitore.getNome(), fornitore.getEmail(),
                fornitore.getApiEndpoint(), fornitore.isDisponibile());
    }

    private Prodotto toProdotto(ProdottoBean prodottoBean) {
        return new Prodotto(prodottoBean.getId(), prodottoBean.getNome(), prodottoBean.getCategoria(),
                prodottoBean.getQuantita(), prodottoBean.getSogliaMinima(), prodottoBean.getPrezzoUnitario());
    }

    private Ordine toOrdine(OrdineBean ordineBean) {
        String idOrdine = ordineBean.getIdOrdine();
        if (idOrdine == null || idOrdine.isBlank()) {
            idOrdine = "ORD-" + UUID.randomUUID();
        }
        return new Ordine(idOrdine, toFornitore(ordineBean.getFornitore()),
                ordineBean.getProdotti().stream().map(this::toProdotto).toList(), ordineBean.getTotale());
    }

    private Fornitore toFornitore(FornitoreBean fornitoreBean) {
        return new Fornitore(fornitoreBean.getId(), fornitoreBean.getNome(), fornitoreBean.getEmail(),
                fornitoreBean.getApiEndpoint(), fornitoreBean.isDisponibile());
    }
}
