package com.stocktrack.boundary;

import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.controller.GestisciInventarioFornitoreController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

/**
 * Boundary BCE del caso d'uso GestisciInventarioFornitore. Riceve bean dalla view, esegue la validazione sintattica, crea il controller applicativo per la richiesta e trasforma eccezioni e risultati in bean di risposta coerenti.
 */
public class GestisciInventarioFornitoreBoundary {

    public FornitoreBean visualizzaProfilo() {
        GestisciInventarioFornitoreController controller = new GestisciInventarioFornitoreController();
        try {
            return controller.visualizzaProfiloFornitoreCorrente();
        } catch (InvalidInputException | PersistenceException e) {
            return new FornitoreBean();
        }
    }

    public EsitoListaBean<ProdottoBean> visualizzaInventarioConEsito() {
        GestisciInventarioFornitoreController controller = new GestisciInventarioFornitoreController();
        try {
            List<ProdottoBean> prodotti = controller.visualizzaInventarioCorrente();
            String messaggio = prodotti.isEmpty() ? "Magazzino fornitore vuoto" : "Magazzino fornitore aggiornato";
            return EsitoListaBean.success(messaggio, prodotti);
        } catch (InvalidInputException | PersistenceException | FornitoreConnectionException e) {
            return EsitoListaBean.failure(e.getMessage());
        }
    }

    public EsitoOperazioneBean salvaProdotto(ProdottoBean prodottoBean) {
        GestisciInventarioFornitoreController controller = new GestisciInventarioFornitoreController();
        try {
            validateProdotto(prodottoBean);
            return controller.salvaProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException | FornitoreConnectionException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public void logout() {
        LoginController controller = new LoginController();
        controller.logout();
    }

    private void validateProdotto(ProdottoBean prodottoBean) throws InvalidInputException {
        if (prodottoBean == null) {
            throw new InvalidInputException("Prodotto obbligatorio");
        }
        prodottoBean.validate();
    }
}
