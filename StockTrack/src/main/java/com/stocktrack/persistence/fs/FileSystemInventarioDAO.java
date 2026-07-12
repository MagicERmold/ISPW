package com.stocktrack.persistence.fs;

import com.stocktrack.entity.Inventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.InventarioDAO;

/**
 * Implementazione del DAO che persiste Inventario su file per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class FileSystemInventarioDAO implements InventarioDAO {

    @Override
    public Inventario findInventario() throws PersistenceException {
        return FileSystemDataStore.loadInventario();
    }

    @Override
    public void save(Inventario inventario) throws PersistenceException {
        FileSystemDataStore.saveInventario(inventario);
    }

    @Override
    public void update(Inventario inventario) throws PersistenceException {
        FileSystemDataStore.saveInventario(inventario);
    }
}
