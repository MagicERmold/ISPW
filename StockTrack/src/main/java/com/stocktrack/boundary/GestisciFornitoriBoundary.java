package com.stocktrack.boundary;

import com.stocktrack.bean.CodiceFornitoreBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.controller.GestisciFornitoriController;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public class GestisciFornitoriBoundary {

    public EsitoListaBean<FornitoreBean> visualizzaFornitoriConEsito() {
        GestisciFornitoriController controller = new GestisciFornitoriController();
        try {
            List<FornitoreBean> fornitori = controller.visualizzaFornitori();
            String messaggio = fornitori.isEmpty() ? "Nessun fornitore collegato" : "Fornitori aggiornati";
            return EsitoListaBean.success(messaggio, fornitori);
        } catch (PersistenceException e) {
            return EsitoListaBean.failure("Errore caricamento fornitori: " + e.getMessage());
        }
    }

    public EsitoListaBean<ProdottoBean> visualizzaInventarioFornitoreConEsito(FornitoreBean fornitoreBean) {
        GestisciFornitoriController controller = new GestisciFornitoriController();
        try {
            validateFornitore(fornitoreBean);
            List<ProdottoBean> prodotti = controller.visualizzaInventarioFornitore(fornitoreBean);
            String messaggio = prodotti.isEmpty()
                    ? "Inventario fornitore non disponibile"
                    : "Inventario fornitore caricato";
            return EsitoListaBean.success(messaggio, prodotti);
        } catch (InvalidInputException | FornitoreConnectionException e) {
            return EsitoListaBean.failure(e.getMessage());
        }
    }

    public EsitoOperazioneBean aggiungiFornitoreConCodice(String codiceFornitore) {
        return aggiungiFornitoreConCodice(new CodiceFornitoreBean(codiceFornitore));
    }

    public EsitoOperazioneBean aggiungiFornitoreConCodice(CodiceFornitoreBean codiceFornitoreBean) {
        GestisciFornitoriController controller = new GestisciFornitoriController();
        try {
            codiceFornitoreBean.validate();
            return controller.aggiungiFornitoreConCodice(codiceFornitoreBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean rimuoviFornitore(FornitoreBean fornitoreBean) {
        GestisciFornitoriController controller = new GestisciFornitoriController();
        try {
            validateFornitore(fornitoreBean);
            return controller.rimuoviFornitore(fornitoreBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    private void validateFornitore(FornitoreBean fornitoreBean) throws InvalidInputException {
        if (fornitoreBean == null) {
            throw new InvalidInputException("Selezionare un fornitore");
        }
        fornitoreBean.validate();
    }
}
