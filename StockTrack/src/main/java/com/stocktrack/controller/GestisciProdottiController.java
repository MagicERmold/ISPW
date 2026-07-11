package com.stocktrack.controller;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.ProdottoBean;
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
import java.util.List;
import java.util.UUID;

public class GestisciProdottiController {

    private static final String PRODOTTO_NON_TROVATO = "Prodotto non trovato";

    public List<ProdottoBean> visualizzaProdotti() throws PersistenceException {
        return getProdottoDAO().findAll().stream()
                .map(this::toProdottoBean)
                .toList();
    }

    public EsitoOperazioneBean modificaQuantitaProdotto(String idProdotto, int quantita)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        if (idProdotto == null || idProdotto.isBlank()) {
            throw new InvalidInputException("Selezionare un prodotto");
        }
        if (quantita < 0) {
            throw new InvalidInputException("Quantita prodotto non valida");
        }
        ProdottoDAO prodottoDAO = getProdottoDAO();
        Prodotto prodotto = prodottoDAO.findById(idProdotto)
                .orElseThrow(() -> new InvalidInputException(PRODOTTO_NON_TROVATO));
        prodotto.setQuantita(quantita);
        prodottoDAO.update(prodotto);
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, "Quantita prodotto modificata");
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoBean prodottoBean)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        if (prodottoBean == null || prodottoBean.getId() == null || prodottoBean.getId().isBlank()) {
            throw new InvalidInputException("Id prodotto obbligatorio");
        }
        ProdottoDAO prodottoDAO = getProdottoDAO();
        if (prodottoDAO.findById(prodottoBean.getId()).isEmpty()) {
            return new EsitoOperazioneBean(false, PRODOTTO_NON_TROVATO);
        }
        prodottoDAO.deleteById(prodottoBean.getId());
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, "Prodotto rimosso");
    }

    public EsitoOperazioneBean registraVenditaManuale(String idProdotto, int quantita)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(idProdotto, quantita, TipoMovimentoInventario.VENDITA,
                "Vendita manuale registrata");
    }

    public EsitoOperazioneBean registraAcquistoEsterno(String idProdotto, int quantita)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(idProdotto, quantita, TipoMovimentoInventario.ACQUISTO_ESTERNO,
                "Acquisto esterno registrato");
    }

    protected ProdottoDAO getProdottoDAO() {
        return DAOFactoryProvider.getFactory().getProdottoDAO();
    }

    protected ProdottoBean toProdottoBean(Prodotto prodotto) {
        return new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                prodotto.getQuantita(), prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario());
    }

    private EsitoOperazioneBean aggiornaQuantitaManuale(String idProdotto, int quantita,
                                                        TipoMovimentoInventario tipo, String messaggio)
            throws InvalidInputException, PersistenceException {
        verificaPermessiGestioneProdotti();
        if (idProdotto == null || idProdotto.isBlank()) {
            throw new InvalidInputException("Selezionare un prodotto");
        }
        if (quantita <= 0) {
            throw new InvalidInputException("Quantita movimento non valida");
        }

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
        registraMovimento(prodotto, tipo, quantita, "Inventario");
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, messaggio);
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

    protected void registraMovimento(Prodotto prodotto, TipoMovimentoInventario tipo, int quantita, String origine)
            throws PersistenceException {
        BigDecimal valoreUnitario = prodotto.getPrezzoUnitario() == null ? BigDecimal.ZERO
                : prodotto.getPrezzoUnitario();
        MovimentoInventario movimento = new MovimentoInventario("MOV-" + UUID.randomUUID(), prodotto.getId(),
                prodotto.getNome(), tipo, quantita, valoreUnitario, origine);
        DAOFactoryProvider.getFactory().getMovimentoInventarioDAO().save(movimento);
    }

    private void sincronizzaInventario() throws PersistenceException {
        DAOFactory daoFactory = DAOFactoryProvider.getFactory();
        Inventario inventario = daoFactory.getInventarioDAO().findInventario();
        inventario.setProdotti(new ArrayList<>(daoFactory.getProdottoDAO().findAll()));
        daoFactory.getInventarioDAO().update(inventario);
    }
}
