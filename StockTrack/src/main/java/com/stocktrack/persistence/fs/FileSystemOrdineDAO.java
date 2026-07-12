package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.OrdineDAO;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Ordine su file per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
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
