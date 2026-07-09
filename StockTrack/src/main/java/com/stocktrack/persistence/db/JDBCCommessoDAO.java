package com.stocktrack.persistence.db;

import com.stocktrack.entity.Commesso;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.CommessoDAO;

import java.util.List;
import java.util.Optional;

public class JDBCCommessoDAO implements CommessoDAO {

    @Override
    public Optional<Commesso> findById(String id) throws PersistenceException {
        return JDBCDataStore.findCommessoById(id);
    }

    @Override
    public Optional<Commesso> findByEmail(String email) throws PersistenceException {
        return JDBCDataStore.findCommessoByEmail(email);
    }

    @Override
    public List<Commesso> findAll() throws PersistenceException {
        return JDBCDataStore.loadCommessi();
    }

    @Override
    public void save(Commesso commesso) throws PersistenceException {
        JDBCDataStore.saveCommesso(commesso);
    }
}
