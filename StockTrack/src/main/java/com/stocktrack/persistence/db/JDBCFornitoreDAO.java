package com.stocktrack.persistence.db;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.FornitoreDAO;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Fornitore su database JDBC per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
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
    public Optional<Fornitore> findByEmail(String email) throws PersistenceException {
        return JDBCDataStore.findFornitoreByEmail(email);
    }

    @Override
    public void save(Fornitore fornitore) throws PersistenceException {
        JDBCDataStore.saveFornitore(fornitore);
    }

    @Override
    public void deleteById(String id) throws PersistenceException {
        JDBCDataStore.deleteFornitore(id);
    }
}
