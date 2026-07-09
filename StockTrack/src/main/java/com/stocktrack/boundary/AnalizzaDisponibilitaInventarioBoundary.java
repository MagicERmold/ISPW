package com.stocktrack.boundary;

import com.stocktrack.bean.DisponibilitaProdottoBean;
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
        try {
            return new LoginController().login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public InventarioBean visualizzaInventario() {
        try {
            return new AnalizzaDisponibilitaInventarioController().visualizzaInventario();
        } catch (PersistenceException e) {
            return new InventarioBean();
        }
    }

    public List<DisponibilitaProdottoBean> analizzaDisponibilita() {
        try {
            return new AnalizzaDisponibilitaInventarioController().analizzaDisponibilita();
        } catch (PersistenceException e) {
            return List.of();
        }
    }

    public DisponibilitaProdottoBean verificaDisponibilita(ProdottoBean prodottoBean) {
        try {
            return new AnalizzaDisponibilitaInventarioController().verificaDisponibilita(prodottoBean);
        } catch (InvalidInputException e) {
            return new DisponibilitaProdottoBean(prodottoBean, 0, false, e.getMessage());
        }
    }
}
