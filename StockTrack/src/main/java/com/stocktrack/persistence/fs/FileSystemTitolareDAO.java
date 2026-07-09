package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.TitolareDAO;

import java.util.List;
import java.util.Optional;

public class FileSystemTitolareDAO implements TitolareDAO {

    @Override
    public Optional<Titolare> findById(String id) throws PersistenceException {
        return FileSystemDataStore.findTitolareById(id);
    }

    @Override
    public Optional<Titolare> findByEmail(String email) throws PersistenceException {
        return FileSystemDataStore.findTitolareByEmail(email);
    }

    @Override
    public List<Titolare> findAll() throws PersistenceException {
        return FileSystemDataStore.loadTitolari();
    }

    @Override
    public void save(Titolare titolare) throws PersistenceException {
        FileSystemDataStore.saveTitolare(titolare);
    }
}
