package com.stocktrack.controller;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.factory.DAOFactory;
import com.stocktrack.pattern.factory.DAOFactoryProvider;
import com.stocktrack.persistence.dao.ProdottoDAO;

import java.util.ArrayList;
import java.util.List;

public class GestisciProdottiController {

    public List<ProdottoBean> visualizzaProdotti() throws PersistenceException {
        return getProdottoDAO().findAll().stream()
                .map(this::toProdottoBean)
                .toList();
    }

    public EsitoOperazioneBean aggiungiProdotto(ProdottoBean prodottoBean)
            throws InvalidInputException, PersistenceException {
        prodottoBean.validate();
        ProdottoDAO prodottoDAO = getProdottoDAO();
        if (prodottoDAO.findById(prodottoBean.getId()).isPresent()) {
            return new EsitoOperazioneBean(false, "Prodotto gia presente");
        }
        prodottoDAO.save(toProdotto(prodottoBean));
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, "Prodotto aggiunto");
    }

    public EsitoOperazioneBean modificaProdotto(ProdottoBean prodottoBean)
            throws InvalidInputException, PersistenceException {
        prodottoBean.validate();
        ProdottoDAO prodottoDAO = getProdottoDAO();
        if (prodottoDAO.findById(prodottoBean.getId()).isEmpty()) {
            return new EsitoOperazioneBean(false, "Prodotto non trovato");
        }
        prodottoDAO.update(toProdotto(prodottoBean));
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, "Prodotto modificato");
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoBean prodottoBean)
            throws InvalidInputException, PersistenceException {
        if (prodottoBean == null || prodottoBean.getId() == null || prodottoBean.getId().isBlank()) {
            throw new InvalidInputException("Id prodotto obbligatorio");
        }
        ProdottoDAO prodottoDAO = getProdottoDAO();
        if (prodottoDAO.findById(prodottoBean.getId()).isEmpty()) {
            return new EsitoOperazioneBean(false, "Prodotto non trovato");
        }
        prodottoDAO.deleteById(prodottoBean.getId());
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, "Prodotto rimosso");
    }

    public EsitoOperazioneBean registraVenditaManuale(String idProdotto, int quantita)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(idProdotto, -quantita, "Vendita manuale registrata");
    }

    public EsitoOperazioneBean registraAcquistoEsterno(String idProdotto, int quantita)
            throws InvalidInputException, PersistenceException {
        return aggiornaQuantitaManuale(idProdotto, quantita, "Acquisto esterno registrato");
    }

    protected ProdottoDAO getProdottoDAO() {
        return DAOFactoryProvider.getFactory().getProdottoDAO();
    }

    protected ProdottoBean toProdottoBean(Prodotto prodotto) {
        return new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                prodotto.getQuantita(), prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario());
    }

    protected Prodotto toProdotto(ProdottoBean prodottoBean) {
        return new Prodotto(prodottoBean.getId(), prodottoBean.getNome(), prodottoBean.getCategoria(),
                prodottoBean.getQuantita(), prodottoBean.getSogliaMinima(), prodottoBean.getPrezzoUnitario());
    }

    private EsitoOperazioneBean aggiornaQuantitaManuale(String idProdotto, int delta, String messaggio)
            throws InvalidInputException, PersistenceException {
        if (idProdotto == null || idProdotto.isBlank()) {
            throw new InvalidInputException("Selezionare un prodotto");
        }
        if (delta == 0) {
            throw new InvalidInputException("Quantita movimento non valida");
        }

        ProdottoDAO prodottoDAO = getProdottoDAO();
        Prodotto prodotto = prodottoDAO.findById(idProdotto)
                .orElseThrow(() -> new InvalidInputException("Prodotto non trovato"));
        int nuovaQuantita = prodotto.getQuantita() + delta;
        if (nuovaQuantita < 0) {
            throw new InvalidInputException("Quantita insufficiente in inventario");
        }
        prodotto.setQuantita(nuovaQuantita);
        prodottoDAO.update(prodotto);
        sincronizzaInventario();
        return new EsitoOperazioneBean(true, messaggio);
    }

    private void sincronizzaInventario() throws PersistenceException {
        DAOFactory daoFactory = DAOFactoryProvider.getFactory();
        Inventario inventario = daoFactory.getInventarioDAO().findInventario();
        inventario.setProdotti(new ArrayList<>(daoFactory.getProdottoDAO().findAll()));
        daoFactory.getInventarioDAO().update(inventario);
    }
}
