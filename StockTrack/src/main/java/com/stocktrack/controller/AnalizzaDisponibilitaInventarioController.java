package com.stocktrack.controller;

import com.stocktrack.bean.DisponibilitaProdottoBean;
import com.stocktrack.bean.InventarioBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.entity.Inventario;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.factory.DAOFactoryProvider;

import java.util.List;

public class AnalizzaDisponibilitaInventarioController {

    public InventarioBean visualizzaInventario() throws PersistenceException {
        Inventario inventario = DAOFactoryProvider.getFactory().getInventarioDAO().findInventario();
        return new InventarioBean(inventario.getProdotti().stream()
                .map(this::toProdottoBean)
                .toList());
    }

    public List<DisponibilitaProdottoBean> analizzaDisponibilita() throws PersistenceException {
        return visualizzaInventario().getProdotti().stream()
                .map(this::toDisponibilitaProdottoBean)
                .toList();
    }

    public DisponibilitaProdottoBean verificaDisponibilita(ProdottoBean prodottoBean) {
        return toDisponibilitaProdottoBean(prodottoBean);
    }

    private DisponibilitaProdottoBean toDisponibilitaProdottoBean(ProdottoBean prodottoBean) {
        boolean disponibile = prodottoBean.getQuantita() > 0;
        String messaggio = disponibile ? "Disponibile" : "Non disponibile";
        if (prodottoBean.isSottoSoglia()) {
            messaggio = disponibile ? "Sotto soglia" : "Esaurito";
        }
        return new DisponibilitaProdottoBean(prodottoBean, prodottoBean.getQuantita(), disponibile, messaggio);
    }

    private ProdottoBean toProdottoBean(Prodotto prodotto) {
        return new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                prodotto.getQuantita(), prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario());
    }
}
