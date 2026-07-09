package com.stocktrack.boundary;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.controller.AnalizzaStatisticheVenditaController;
import com.stocktrack.controller.GestisciProdottiController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public class GestisciProdottiBoundary {

    private final GestisciProdottiController gestisciProdottiController = new GestisciProdottiController();
    private final AnalizzaStatisticheVenditaController statisticheVenditaController =
            new AnalizzaStatisticheVenditaController();

    public ProfiloUtenteBean login(LoginBean loginBean) {
        try {
            return new LoginController().login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public List<ProdottoBean> visualizzaProdotti() {
        try {
            return gestisciProdottiController.visualizzaProdotti();
        } catch (PersistenceException e) {
            return List.of();
        }
    }

    public EsitoOperazioneBean aggiungiProdotto(ProdottoBean prodottoBean) {
        try {
            return gestisciProdottiController.aggiungiProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean modificaProdotto(ProdottoBean prodottoBean) {
        try {
            return gestisciProdottiController.modificaProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoBean prodottoBean) {
        try {
            return gestisciProdottiController.rimuoviProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraVenditaManuale(String idProdotto, int quantita) {
        try {
            return gestisciProdottiController.registraVenditaManuale(idProdotto, quantita);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraAcquistoEsterno(String idProdotto, int quantita) {
        try {
            return gestisciProdottiController.registraAcquistoEsterno(idProdotto, quantita);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public List<StatisticaVenditaMensileBean> analizzaStatisticheVenditaMensili() {
        try {
            return statisticheVenditaController.analizzaStatisticheMensili();
        } catch (PersistenceException e) {
            return List.of();
        }
    }
}
