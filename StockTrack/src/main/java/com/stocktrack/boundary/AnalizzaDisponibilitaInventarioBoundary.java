package com.stocktrack.boundary;

import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.controller.AnalizzaDisponibilitaInventarioController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

/**
 * Boundary BCE del caso d'uso AnalizzaDisponibilitaInventario. Riceve bean dalla view, esegue la validazione sintattica, crea il controller applicativo per la richiesta e trasforma eccezioni e risultati in bean di risposta coerenti.
 */
public class AnalizzaDisponibilitaInventarioBoundary {

    public ProfiloUtenteBean login(LoginBean loginBean) {
        LoginController controller = new LoginController();
        try {
            validateLogin(loginBean);
            return controller.login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public EsitoListaBean<DisponibilitaProdottoBean> analizzaDisponibilitaConEsito() {
        AnalizzaDisponibilitaInventarioController controller = new AnalizzaDisponibilitaInventarioController();
        try {
            List<DisponibilitaProdottoBean> disponibilita = controller.analizzaDisponibilita();
            String messaggio = disponibilita.isEmpty() ? "Inventario vuoto" : "Inventario aggiornato";
            return EsitoListaBean.success(messaggio, disponibilita);
        } catch (PersistenceException e) {
            return EsitoListaBean.failure("Errore caricamento inventario: " + e.getMessage());
        }
    }

    private void validateLogin(LoginBean loginBean) throws InvalidInputException {
        if (loginBean == null) {
            throw new InvalidInputException("Credenziali obbligatorie");
        }
        loginBean.validate();
    }
}
