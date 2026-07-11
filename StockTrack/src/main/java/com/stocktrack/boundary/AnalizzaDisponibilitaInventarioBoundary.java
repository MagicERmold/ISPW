package com.stocktrack.boundary;

import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.InventarioBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.controller.AnalizzaDisponibilitaInventarioController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

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

    public InventarioBean visualizzaInventario() {
        AnalizzaDisponibilitaInventarioController controller = new AnalizzaDisponibilitaInventarioController();
        try {
            return controller.visualizzaInventario();
        } catch (PersistenceException e) {
            return new InventarioBean();
        }
    }

    public List<DisponibilitaProdottoBean> analizzaDisponibilita() {
        return analizzaDisponibilitaConEsito().getElementi();
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

    public DisponibilitaProdottoBean verificaDisponibilita(ProdottoBean prodottoBean) {
        AnalizzaDisponibilitaInventarioController controller = new AnalizzaDisponibilitaInventarioController();
        try {
            validateProdotto(prodottoBean);
            return controller.verificaDisponibilita(prodottoBean);
        } catch (InvalidInputException e) {
            return new DisponibilitaProdottoBean(prodottoBean, 0, false, e.getMessage());
        }
    }

    private void validateProdotto(ProdottoBean prodottoBean) throws InvalidInputException {
        if (prodottoBean == null) {
            throw new InvalidInputException("Prodotto obbligatorio");
        }
        prodottoBean.validate();
    }

    private void validateLogin(LoginBean loginBean) throws InvalidInputException {
        if (loginBean == null) {
            throw new InvalidInputException("Credenziali obbligatorie");
        }
        loginBean.validate();
    }
}
