package com.stocktrack.persistence.fs;

import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.MovimentoInventarioDAO;

import java.util.List;

/**
 * Implementazione del DAO che persiste MovimentoInventario su file per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class FileSystemMovimentoInventarioDAO implements MovimentoInventarioDAO {

    @Override
    public void save(MovimentoInventario movimento) throws PersistenceException {
        FileSystemDataStore.saveMovimentoInventario(movimento);
    }

    @Override
    public List<MovimentoInventario> findAll() throws PersistenceException {
        return FileSystemDataStore.loadMovimentiInventario();
    }
}
