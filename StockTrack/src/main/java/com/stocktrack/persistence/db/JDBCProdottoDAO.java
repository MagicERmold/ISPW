package com.stocktrack.persistence.db;

import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.ProdottoDAO;

import java.util.List;
import java.util.Optional;

public class JDBCProdottoDAO implements ProdottoDAO {

    @Override
    public List<Prodotto> findAll() throws PersistenceException {
        return JDBCDataStore.loadProdotti();
    }

    @Override
    public Optional<Prodotto> findById(String id) throws PersistenceException {
        return JDBCDataStore.findProdottoById(id);
    }

    @Override
    public void save(Prodotto prodotto) throws PersistenceException {
        JDBCDataStore.saveProdotto(prodotto);
    }

    @Override
    public void update(Prodotto prodotto) throws PersistenceException {
        JDBCDataStore.saveProdotto(prodotto);
    }

    @Override
    public void deleteById(String id) throws PersistenceException {
        JDBCDataStore.deleteProdotto(id);
    }
}
