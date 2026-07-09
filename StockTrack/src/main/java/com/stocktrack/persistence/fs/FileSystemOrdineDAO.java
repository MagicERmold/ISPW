package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.OrdineDAO;

import java.util.List;
import java.util.Optional;

public class FileSystemOrdineDAO implements OrdineDAO {

    @Override
    public void save(Ordine ordine) throws PersistenceException {
        FileSystemDataStore.saveOrdine(ordine);
    }

    @Override
    public Optional<Ordine> findById(String id) throws PersistenceException {
        return FileSystemDataStore.findOrdineById(id);
    }

    @Override
    public List<Ordine> findAll() throws PersistenceException {
        return FileSystemDataStore.loadOrdini();
    }
}
