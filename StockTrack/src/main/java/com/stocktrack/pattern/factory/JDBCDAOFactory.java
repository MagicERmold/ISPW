package com.stocktrack.pattern.factory;

import com.stocktrack.persistence.dao.CommessoDAO;
import com.stocktrack.persistence.dao.FornitoreDAO;
import com.stocktrack.persistence.dao.InventarioDAO;
import com.stocktrack.persistence.dao.OrdineDAO;
import com.stocktrack.persistence.dao.ProdottoDAO;
import com.stocktrack.persistence.dao.TitolareDAO;
import com.stocktrack.persistence.db.JDBCCommessoDAO;
import com.stocktrack.persistence.db.JDBCFornitoreDAO;
import com.stocktrack.persistence.db.JDBCInventarioDAO;
import com.stocktrack.persistence.db.JDBCOrdineDAO;
import com.stocktrack.persistence.db.JDBCProdottoDAO;
import com.stocktrack.persistence.db.JDBCTitolareDAO;

public class JDBCDAOFactory extends DAOFactory {

    @Override
    public TitolareDAO getTitolareDAO() {
        return new JDBCTitolareDAO();
    }

    @Override
    public CommessoDAO getCommessoDAO() {
        return new JDBCCommessoDAO();
    }

    @Override
    public FornitoreDAO getFornitoreDAO() {
        return new JDBCFornitoreDAO();
    }

    @Override
    public ProdottoDAO getProdottoDAO() {
        return new JDBCProdottoDAO();
    }

    @Override
    public InventarioDAO getInventarioDAO() {
        return new JDBCInventarioDAO();
    }

    @Override
    public OrdineDAO getOrdineDAO() {
        return new JDBCOrdineDAO();
    }
}
