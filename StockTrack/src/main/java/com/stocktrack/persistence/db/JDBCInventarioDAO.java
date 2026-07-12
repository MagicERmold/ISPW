package com.stocktrack.persistence.db;

import com.stocktrack.entity.Inventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.persistence.dao.InventarioDAO;

/**
 * Implementazione del DAO che persiste Inventario su database JDBC per la modalità FULL. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class JDBCInventarioDAO implements InventarioDAO {

    @Override
    public Inventario findInventario() throws PersistenceException {
        return JDBCDataStore.loadInventario();
    }

    @Override
    public void save(Inventario inventario) throws PersistenceException {
        JDBCDataStore.saveInventario(inventario);
    }

    @Override
    public void update(Inventario inventario) throws PersistenceException {
        JDBCDataStore.saveInventario(inventario);
    }
}
