package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.ProdottoDAO;

import java.util.List;
import java.util.Optional;

public class FileSystemProdottoDAO implements ProdottoDAO {

    @Override
    public List<Prodotto> findAll() throws PersistenceException {
        return FileSystemDataStore.loadProdotti();
    }

    @Override
    public Optional<Prodotto> findById(String id) throws PersistenceException {
        return FileSystemDataStore.findProdottoById(id);
    }

    @Override
    public void save(Prodotto prodotto) throws PersistenceException {
        FileSystemDataStore.saveProdotto(prodotto);
    }

    @Override
    public void update(Prodotto prodotto) throws PersistenceException {
        FileSystemDataStore.saveProdotto(prodotto);
    }

    @Override
    public void deleteById(String id) throws PersistenceException {
        FileSystemDataStore.deleteProdotto(id);
    }
}
