package com.stocktrack.pattern.factory;

import com.stocktrack.persistence.dao.CommessoDAO;
import com.stocktrack.persistence.dao.FornitoreDAO;
import com.stocktrack.persistence.dao.InventarioDAO;
import com.stocktrack.persistence.dao.MovimentoInventarioDAO;
import com.stocktrack.persistence.dao.OrdineDAO;
import com.stocktrack.persistence.dao.ProdottoDAO;
import com.stocktrack.persistence.dao.TitolareDAO;
import com.stocktrack.persistence.memory.InMemoryCommessoDAO;
import com.stocktrack.persistence.memory.InMemoryFornitoreDAO;
import com.stocktrack.persistence.memory.InMemoryInventarioDAO;
import com.stocktrack.persistence.memory.InMemoryMovimentoInventarioDAO;
import com.stocktrack.persistence.memory.InMemoryOrdineDAO;
import com.stocktrack.persistence.memory.InMemoryProdottoDAO;
import com.stocktrack.persistence.memory.InMemoryTitolareDAO;

/**
 * Factory concreta che crea la famiglia di DAO Memory. È selezionata dal provider e permette ai controller di usare la persistenza configurata senza cambiare il flusso BCE.
 */
public class MemoryDAOFactory extends DAOFactory {

    @Override
    public TitolareDAO getTitolareDAO() {
        return new InMemoryTitolareDAO();
    }

    @Override
    public CommessoDAO getCommessoDAO() {
        return new InMemoryCommessoDAO();
    }

    @Override
    public FornitoreDAO getFornitoreDAO() {
        return new InMemoryFornitoreDAO();
    }

    @Override
    public ProdottoDAO getProdottoDAO() {
        return new InMemoryProdottoDAO();
    }

    @Override
    public InventarioDAO getInventarioDAO() {
        return new InMemoryInventarioDAO();
    }

    @Override
    public OrdineDAO getOrdineDAO() {
        return new InMemoryOrdineDAO();
    }

    @Override
    public MovimentoInventarioDAO getMovimentoInventarioDAO() {
        return new InMemoryMovimentoInventarioDAO();
    }
}
