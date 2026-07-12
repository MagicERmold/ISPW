package com.stocktrack.persistence.db;

import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.MovimentoInventarioDAO;

import java.util.List;

/**
 * Implementazione del DAO che persiste MovimentoInventario su database JDBC per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class JDBCMovimentoInventarioDAO implements MovimentoInventarioDAO {

    @Override
    public void save(MovimentoInventario movimento) throws PersistenceException {
        JDBCDataStore.saveMovimentoInventario(movimento);
    }

    @Override
    public List<MovimentoInventario> findAll() throws PersistenceException {
        return JDBCDataStore.loadMovimentiInventario();
    }
}
