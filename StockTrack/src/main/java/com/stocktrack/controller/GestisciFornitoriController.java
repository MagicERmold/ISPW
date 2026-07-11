package com.stocktrack.controller;

import com.stocktrack.bean.CodiceFornitoreBean;
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
import com.stocktrack.pattern.singleton.SessionManagerSingleton;

import java.util.List;

public class GestisciFornitoriController {

    public List<FornitoreBean> visualizzaFornitori() throws PersistenceException {
        return DAOFactoryProvider.getFactory().getFornitoreDAO().findAll().stream()
                .map(this::toFornitoreBean)
                .toList();
    }

    public List<ProdottoBean> visualizzaInventarioFornitore(FornitoreBean fornitoreBean)
            throws InvalidInputException, FornitoreConnectionException {
        FornitoreApiAdapter fornitoreApiAdapter = new FornitoreApiAdapter();
        return fornitoreApiAdapter.recuperaProdotti(fornitoreBean);
    }

    public EsitoOperazioneBean aggiungiFornitoreConCodice(CodiceFornitoreBean codiceFornitoreBean)
            throws InvalidInputException, PersistenceException {
        if (!isTitolare()) {
            return new EsitoOperazioneBean(false, "Solo il titolare puo aggiungere fornitori");
        }

        FornitoreApiAdapter fornitoreApiAdapter = new FornitoreApiAdapter();
        FornitoreBean fornitoreBean = fornitoreApiAdapter.recuperaFornitoreDaCodice(
                        codiceFornitoreBean.getCodiceFornitore())
                .orElseThrow(() -> new InvalidInputException("Codice fornitore non riconosciuto"));
        if (DAOFactoryProvider.getFactory().getFornitoreDAO().findById(fornitoreBean.getId()).isPresent()) {
            return new EsitoOperazioneBean(false, "Fornitore gia presente");
        }
        DAOFactoryProvider.getFactory().getFornitoreDAO().save(toFornitore(fornitoreBean));
        return new EsitoOperazioneBean(true, "Fornitore aggiunto: " + fornitoreBean.getNome());
    }

    public EsitoOperazioneBean rimuoviFornitore(FornitoreBean fornitoreBean)
            throws InvalidInputException, PersistenceException {
        if (!isTitolare()) {
            return new EsitoOperazioneBean(false, "Solo il titolare puo rimuovere fornitori");
        }
        if (DAOFactoryProvider.getFactory().getFornitoreDAO().findById(fornitoreBean.getId()).isEmpty()) {
            return new EsitoOperazioneBean(false, "Fornitore non trovato");
        }
        DAOFactoryProvider.getFactory().getFornitoreDAO().deleteById(fornitoreBean.getId());
        return new EsitoOperazioneBean(true, "Fornitore rimosso: " + fornitoreBean.getNome());
    }

    private boolean isTitolare() {
        return SessionManagerSingleton.getInstance()
                .getCurrentSession()
                .map(session -> RuoloUtente.TITOLARE.equals(session.getRuolo()))
                .orElse(false);
    }

    private FornitoreBean toFornitoreBean(Fornitore fornitore) {
        return new FornitoreBean(fornitore.getId(), fornitore.getNome(), fornitore.getEmail(),
                fornitore.getApiEndpoint(), fornitore.isDisponibile());
    }

    private Fornitore toFornitore(FornitoreBean fornitoreBean) {
        return new Fornitore(fornitoreBean.getId(), fornitoreBean.getNome(), fornitoreBean.getEmail(),
                fornitoreBean.getApiEndpoint(), fornitoreBean.isDisponibile());
    }
}
