package com.stocktrack.persistence.db;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.FornitoreDAO;

import java.util.List;
import java.util.Optional;

public class JDBCFornitoreDAO implements FornitoreDAO {

    @Override
    public List<Fornitore> findAll() throws PersistenceException {
        return JDBCDataStore.loadFornitori();
    }

    @Override
    public Optional<Fornitore> findById(String id) throws PersistenceException {
        return JDBCDataStore.findFornitoreById(id);
    }

    @Override
    public void save(Fornitore fornitore) throws PersistenceException {
        JDBCDataStore.saveFornitore(fornitore);
    }
}
