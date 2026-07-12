package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Inventario;
import com.stocktrack.persistence.dao.InventarioDAO;

/**
 * Implementazione del DAO che persiste Inventario in memoria per la modalità DEMO. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class InMemoryInventarioDAO implements InventarioDAO {

    @Override
    public Inventario findInventario() {
        return InMemoryDataStore.getInventario();
    }

    @Override
    public void save(Inventario inventario) {
        InMemoryDataStore.setInventario(inventario);
    }

    @Override
    public void update(Inventario inventario) {
        InMemoryDataStore.setInventario(inventario);
    }
}
