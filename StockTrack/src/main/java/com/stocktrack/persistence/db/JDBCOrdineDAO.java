package com.stocktrack.persistence.db;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.OrdineDAO;

import java.util.List;
import java.util.Optional;

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
