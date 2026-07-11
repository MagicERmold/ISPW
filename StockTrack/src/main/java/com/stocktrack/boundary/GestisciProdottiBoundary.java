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

    public ProfiloUtenteBean login(LoginBean loginBean) {
        try {
            return new LoginController().login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public List<ProdottoBean> visualizzaProdotti() {
        try {
            return new GestisciProdottiController().visualizzaProdotti();
        } catch (PersistenceException e) {
            return List.of();
        }
    }

    public EsitoOperazioneBean modificaQuantitaProdotto(String idProdotto, int quantita) {
        try {
            return new GestisciProdottiController().modificaQuantitaProdotto(idProdotto, quantita);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean rimuoviProdotto(ProdottoBean prodottoBean) {
        try {
            return new GestisciProdottiController().rimuoviProdotto(prodottoBean);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraVenditaManuale(String idProdotto, int quantita) {
        try {
            return new GestisciProdottiController().registraVenditaManuale(idProdotto, quantita);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public EsitoOperazioneBean registraAcquistoEsterno(String idProdotto, int quantita) {
        try {
            return new GestisciProdottiController().registraAcquistoEsterno(idProdotto, quantita);
        } catch (InvalidInputException | PersistenceException e) {
            return new EsitoOperazioneBean(false, e.getMessage());
        }
    }

    public List<StatisticaVenditaMensileBean> analizzaStatisticheVenditaMensili() {
        try {
            return new AnalizzaStatisticheVenditaController().analizzaStatisticheMensili();
        } catch (PersistenceException e) {
            return List.of();
        }
    }
}
