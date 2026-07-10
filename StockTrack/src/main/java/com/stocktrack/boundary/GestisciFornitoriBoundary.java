package com.stocktrack.boundary;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.controller.GestisciFornitoriController;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public class GestisciFornitoriBoundary {

    private final GestisciFornitoriController controller = new GestisciFornitoriController();

    public List<FornitoreBean> visualizzaFornitori() {
        try {
            return controller.visualizzaFornitori();
        } catch (PersistenceException e) {
            return List.of();
        }
    }

    public List<ProdottoBean> visualizzaInventarioFornitore(FornitoreBean fornitoreBean) {
        try {
            return controller.visualizzaInventarioFornitore(fornitoreBean);
        } catch (InvalidInputException | FornitoreConnectionException e) {
            return List.of();
        }
    }

    public EsitoOperazioneBean aggiungiFornitoreConCodice(String codiceFornitore) {
        try {
            return controller.aggiungiFornitoreConCodice(codiceFornitore);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean rimuoviFornitore(FornitoreBean fornitoreBean) {
        try {
            return controller.rimuoviFornitore(fornitoreBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }
}
