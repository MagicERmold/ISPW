package com.stocktrack.controller;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.adapter.FornitoreApiAdapter;
import com.stocktrack.pattern.factory.DAOFactoryProvider;
import com.stocktrack.pattern.singleton.Session;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;

import java.util.List;

public class GestisciInventarioFornitoreController {

    private final FornitoreApiAdapter fornitoreApiAdapter = new FornitoreApiAdapter();

    public FornitoreBean visualizzaProfiloFornitoreCorrente() throws InvalidInputException, PersistenceException {
        return toFornitoreBean(getFornitoreCorrente());
    }

    public List<ProdottoBean> visualizzaInventarioCorrente()
            throws InvalidInputException, PersistenceException, FornitoreConnectionException {
        return fornitoreApiAdapter.recuperaProdotti(toFornitoreBean(getFornitoreCorrente()));
    }

    public EsitoOperazioneBean salvaProdotto(ProdottoBean prodottoBean)
            throws InvalidInputException, PersistenceException, FornitoreConnectionException {
        prodottoBean.validate();
        fornitoreApiAdapter.salvaProdotto(toFornitoreBean(getFornitoreCorrente()), prodottoBean);
        return new EsitoOperazioneBean(true, "Prodotto fornitore salvato");
    }

    private Fornitore getFornitoreCorrente() throws InvalidInputException, PersistenceException {
        Session session = SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .filter(currentSession -> RuoloUtente.FORNITORE.equals(currentSession.getRuolo()))
                .orElseThrow(() -> new InvalidInputException("Sessione fornitore non valida"));
        return DAOFactoryProvider.getFactory().getFornitoreDAO().findById(session.getIdUtente())
                .orElseThrow(() -> new InvalidInputException("Fornitore non trovato"));
    }

    private FornitoreBean toFornitoreBean(Fornitore fornitore) {
        return new FornitoreBean(fornitore.getId(), fornitore.getNome(), fornitore.getEmail(),
                fornitore.getApiEndpoint(), fornitore.isDisponibile());
    }
}
