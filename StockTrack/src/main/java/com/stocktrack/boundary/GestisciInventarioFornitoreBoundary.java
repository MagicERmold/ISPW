package com.stocktrack.boundary;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.controller.GestisciInventarioFornitoreController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public class GestisciInventarioFornitoreBoundary {

    private final GestisciInventarioFornitoreController controller = new GestisciInventarioFornitoreController();

    public FornitoreBean visualizzaProfilo() {
        try {
            return controller.visualizzaProfiloFornitoreCorrente();
        } catch (InvalidInputException | PersistenceException e) {
            return new FornitoreBean();
        }
    }

    public List<ProdottoBean> visualizzaInventario() {
        try {
            return controller.visualizzaInventarioCorrente();
        } catch (InvalidInputException | PersistenceException | FornitoreConnectionException e) {
            return List.of();
        }
    }

    public EsitoOperazioneBean salvaProdotto(ProdottoBean prodottoBean) {
        try {
            return controller.salvaProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException | FornitoreConnectionException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean logout() {
        return new LoginController().logout();
    }
}
