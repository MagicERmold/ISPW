package com.stocktrack.boundary;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.ProdottoSelezionatoBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.QuantitaProdottoBean;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.controller.AnalizzaStatisticheVenditaController;
import com.stocktrack.controller.GestisciProdottiController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public class GestisciProdottiBoundary {

    public ProfiloUtenteBean login(LoginBean loginBean) {
        LoginController controller = new LoginController();
        try {
            validateLogin(loginBean);
            return controller.login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public List<ProdottoBean> visualizzaProdotti() {
        return visualizzaProdottiConEsito().getElementi();
    }

    public EsitoListaBean<ProdottoBean> visualizzaProdottiConEsito() {
        GestisciProdottiController controller = new GestisciProdottiController();
        try {
            List<ProdottoBean> prodotti = controller.visualizzaProdotti();
            String messaggio = prodotti.isEmpty() ? "Inventario vuoto" : "Prodotti caricati";
            return EsitoListaBean.success(messaggio, prodotti);
        } catch (PersistenceException e) {
            return EsitoListaBean.failure("Errore caricamento prodotti: " + e.getMessage());
        }
    }

    public EsitoOperazioneBean modificaQuantitaProdotto(String idProdotto, int quantita) {
        return modificaQuantitaProdotto(new QuantitaProdottoBean(idProdotto, quantita));
    }

    public EsitoOperazioneBean modificaQuantitaProdotto(QuantitaProdottoBean quantitaProdottoBean) {
        GestisciProdottiController controller = new GestisciProdottiController();
        try {
            quantitaProdottoBean.validate();
            return controller.modificaQuantitaProdotto(quantitaProdottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoBean prodottoBean) {
        return rimuoviProdotto(new ProdottoSelezionatoBean(prodottoBean == null ? null : prodottoBean.getId()));
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoSelezionatoBean prodottoSelezionatoBean) {
        GestisciProdottiController controller = new GestisciProdottiController();
        try {
            prodottoSelezionatoBean.validate();
            return controller.rimuoviProdotto(prodottoSelezionatoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraVenditaManuale(String idProdotto, int quantita) {
        return registraVenditaManuale(new QuantitaProdottoBean(idProdotto, quantita));
    }

    public EsitoOperazioneBean registraVenditaManuale(QuantitaProdottoBean movimentoProdottoBean) {
        GestisciProdottiController controller = new GestisciProdottiController();
        try {
            movimentoProdottoBean.validateMovimento();
            return controller.registraVenditaManuale(movimentoProdottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraAcquistoEsterno(String idProdotto, int quantita) {
        return registraAcquistoEsterno(new QuantitaProdottoBean(idProdotto, quantita));
    }

    public EsitoOperazioneBean registraAcquistoEsterno(QuantitaProdottoBean movimentoProdottoBean) {
        GestisciProdottiController controller = new GestisciProdottiController();
        try {
            movimentoProdottoBean.validateMovimento();
            return controller.registraAcquistoEsterno(movimentoProdottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public List<StatisticaVenditaMensileBean> analizzaStatisticheVenditaMensili() {
        return analizzaStatisticheVenditaMensiliConEsito().getElementi();
    }

    public EsitoListaBean<StatisticaVenditaMensileBean> analizzaStatisticheVenditaMensiliConEsito() {
        AnalizzaStatisticheVenditaController controller = new AnalizzaStatisticheVenditaController();
        try {
            List<StatisticaVenditaMensileBean> statistiche = controller.analizzaStatisticheMensili();
            String messaggio = statistiche.isEmpty() ? "Statistiche non disponibili" : "Statistiche caricate";
            return EsitoListaBean.success(messaggio, statistiche);
        } catch (PersistenceException e) {
            return EsitoListaBean.failure("Errore caricamento statistiche: " + e.getMessage());
        }
    }

    private void validateLogin(LoginBean loginBean) throws InvalidInputException {
        if (loginBean == null) {
            throw new InvalidInputException("Credenziali obbligatorie");
        }
        loginBean.validate();
    }
}
