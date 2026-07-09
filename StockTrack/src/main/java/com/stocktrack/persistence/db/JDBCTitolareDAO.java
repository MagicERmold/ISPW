package com.stocktrack.persistence.db;

import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.TitolareDAO;

import java.util.List;
import java.util.Optional;

public class JDBCTitolareDAO implements TitolareDAO {

    @Override
    public Optional<Titolare> findById(String id) throws PersistenceException {
        return JDBCDataStore.findTitolareById(id);
    }

    @Override
    public Optional<Titolare> findByEmail(String email) throws PersistenceException {
        return JDBCDataStore.findTitolareByEmail(email);
    }

    @Override
    public List<Titolare> findAll() throws PersistenceException {
        return JDBCDataStore.loadTitolari();
    }

    @Override
    public void save(Titolare titolare) throws PersistenceException {
        JDBCDataStore.saveTitolare(titolare);
    }
}
