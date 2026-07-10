package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.FornitoreDAO;

import java.util.List;
import java.util.Optional;

public class FileSystemFornitoreDAO implements FornitoreDAO {

    @Override
    public List<Fornitore> findAll() throws PersistenceException {
        return FileSystemDataStore.loadFornitori();
    }

    @Override
    public Optional<Fornitore> findById(String id) throws PersistenceException {
        return FileSystemDataStore.findFornitoreById(id);
    }

    @Override
    public Optional<Fornitore> findByEmail(String email) throws PersistenceException {
        return FileSystemDataStore.findFornitoreByEmail(email);
    }

    @Override
    public void save(Fornitore fornitore) throws PersistenceException {
        FileSystemDataStore.saveFornitore(fornitore);
    }

    @Override
    public void deleteById(String id) throws PersistenceException {
        FileSystemDataStore.deleteFornitore(id);
    }
}
