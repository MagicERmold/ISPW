package com.stocktrack.controller;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.ProdottoSelezionatoBean;
import com.stocktrack.bean.QuantitaProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.TipoMovimentoInventario;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.factory.DAOFactory;
import com.stocktrack.pattern.factory.DAOFactoryProvider;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.persistence.dao.ProdottoDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class GestisciProdottiController {

    private static final String PRODOTTO_NON_TROVATO = "Prodotto non trovato";
    private static final Object PRODUCT_MANAGEMENT_LOCK = new Object();

    public EsitoOperazioneBean modificaQuantitaProdotto(QuantitaProdottoBean quantitaProdottoBean)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        synchronized (PRODUCT_MANAGEMENT_LOCK) {
            String idProdotto = quantitaProdottoBean.getIdProdotto();
            int quantita = quantitaProdottoBean.getQuantita();
            ProdottoDAO prodottoDAO = getProdottoDAO();
            Prodotto prodotto = prodottoDAO.findById(idProdotto)
                    .orElseThrow(() -> new InvalidInputException(PRODOTTO_NON_TROVATO));
            prodotto.setQuantita(quantita);
            prodottoDAO.update(prodotto);
            sincronizzaInventario();
        }
        return new EsitoOperazioneBean(true, "Quantita prodotto modificata");
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoSelezionatoBean prodottoSelezionatoBean)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        synchronized (PRODUCT_MANAGEMENT_LOCK) {
            String idProdotto = prodottoSelezionatoBean.getIdProdotto();
            ProdottoDAO prodottoDAO = getProdottoDAO();
            if (prodottoDAO.findById(idProdotto).isEmpty()) {
                return new EsitoOperazioneBean(false, PRODOTTO_NON_TROVATO);
            }
            prodottoDAO.deleteById(idProdotto);
            sincronizzaInventario();
        }
        return new EsitoOperazioneBean(true, "Prodotto rimosso");
    }

    public EsitoOperazioneBean registraVenditaManuale(QuantitaProdottoBean movimentoProdottoBean)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(movimentoProdottoBean, TipoMovimentoInventario.VENDITA,
                "Vendita manuale registrata");
    }

    public EsitoOperazioneBean registraAcquistoEsterno(QuantitaProdottoBean movimentoProdottoBean)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(movimentoProdottoBean, TipoMovimentoInventario.ACQUISTO_ESTERNO,
                "Acquisto esterno registrato");
    }

    protected ProdottoDAO getProdottoDAO() {
        return DAOFactoryProvider.getFactory().getProdottoDAO();
    }

    private EsitoOperazioneBean aggiornaQuantitaManuale(QuantitaProdottoBean movimentoProdottoBean,
                                                        TipoMovimentoInventario tipo, String messaggio)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        synchronized (PRODUCT_MANAGEMENT_LOCK) {
            String idProdotto = movimentoProdottoBean.getIdProdotto();
            int quantita = movimentoProdottoBean.getQuantita();

            Prodotto prodotto = getProdotto(tipo, idProdotto, quantita);
            registraMovimento(prodotto, tipo, quantita);
            sincronizzaInventario();
        }
        return new EsitoOperazioneBean(true, messaggio);
    }

    private Prodotto getProdotto(TipoMovimentoInventario tipo, String idProdotto, int quantita) throws InvalidInputException, PersistenceException {
        ProdottoDAO prodottoDAO = getProdottoDAO();
        Prodotto prodotto = prodottoDAO.findById(idProdotto)
                .orElseThrow(() -> new InvalidInputException(PRODOTTO_NON_TROVATO));
        int delta = TipoMovimentoInventario.VENDITA.equals(tipo) ? -quantita : quantita;
        int nuovaQuantita = prodotto.getQuantita() + delta;
        if (nuovaQuantita < 0) {
            throw new InvalidInputException("Quantita insufficiente in inventario");
        }
        prodotto.setQuantita(nuovaQuantita);
        prodottoDAO.update(prodotto);
        return prodotto;
    }

    private void verificaPermessiGestioneProdotti() throws InvalidInputException {
        boolean autorizzato = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> RuoloUtente.TITOLARE.equals(session.getRuolo())
                        || RuoloUtente.COMMESSO.equals(session.getRuolo()))
                .orElse(false);
        if (!autorizzato) {
            throw new InvalidInputException("Solo titolare o commesso possono gestire i prodotti");
        }
    }

    protected void registraMovimento(Prodotto prodotto, TipoMovimentoInventario tipo, int quantita)
            throws PersistenceException {
        BigDecimal valoreUnitario = prodotto.getPrezzoUnitario() == null ? BigDecimal.ZERO
                : prodotto.getPrezzoUnitario();
        MovimentoInventario movimento = new MovimentoInventario("MOV-" + UUID.randomUUID(), prodotto.getId(),
                prodotto.getNome(), tipo, quantita, valoreUnitario, "Inventario");
        DAOFactoryProvider.getFactory().getMovimentoInventarioDAO().save(movimento);
    }

    private void sincronizzaInventario() throws PersistenceException {
        DAOFactory daoFactory = DAOFactoryProvider.getFactory();
        Inventario inventario = daoFactory.getInventarioDAO().findInventario();
        inventario.setProdotti(new ArrayList<>(daoFactory.getProdottoDAO().findAll()));
        daoFactory.getInventarioDAO().update(inventario);
    }
}
