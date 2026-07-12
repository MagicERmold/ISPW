package com.stocktrack.pattern.factory;

import com.stocktrack.persistence.dao.CommessoDAO;
import com.stocktrack.persistence.dao.FornitoreDAO;
import com.stocktrack.persistence.dao.InventarioDAO;
import com.stocktrack.persistence.dao.MovimentoInventarioDAO;
import com.stocktrack.persistence.dao.OrdineDAO;
import com.stocktrack.persistence.dao.ProdottoDAO;
import com.stocktrack.persistence.dao.TitolareDAO;

/**
 * Abstract Factory che definisce la famiglia completa di DAO necessaria ai controller. Serve a cambiare tecnologia di persistenza mantenendo invariata la logica BCE.
 */
public abstract class DAOFactory {

    public abstract TitolareDAO getTitolareDAO();

    public abstract CommessoDAO getCommessoDAO();

    public abstract FornitoreDAO getFornitoreDAO();

    public abstract ProdottoDAO getProdottoDAO();

    public abstract InventarioDAO getInventarioDAO();

    public abstract OrdineDAO getOrdineDAO();

    public abstract MovimentoInventarioDAO getMovimentoInventarioDAO();
}
