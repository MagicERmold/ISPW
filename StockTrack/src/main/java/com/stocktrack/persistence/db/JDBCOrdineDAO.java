package com.stocktrack.persistence.db;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.OrdineDAO;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Ordine su database JDBC per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class JDBCOrdineDAO implements OrdineDAO {

    @Override
    public void save(Ordine ordine) throws PersistenceException {
        JDBCDataStore.saveOrdine(ordine);
    }

    @Override
    public Optional<Ordine> findById(String id) throws PersistenceException {
        return JDBCDataStore.findOrdineById(id);
    }

    @Override
    public List<Ordine> findAll() throws PersistenceException {
        return JDBCDataStore.loadOrdini();
    }
}
