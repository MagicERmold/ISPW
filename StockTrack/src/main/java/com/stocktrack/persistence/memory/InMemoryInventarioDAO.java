package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Inventario;
import com.stocktrack.persistence.dao.InventarioDAO;

public class InMemoryInventarioDAO implements InventarioDAO {

    @Override
    public Inventario findInventario() {
        return InMemoryDataStore.inventario;
    }

    @Override
    public void save(Inventario inventario) {
        InMemoryDataStore.inventario = inventario;
    }

    @Override
    public void update(Inventario inventario) {
        InMemoryDataStore.inventario = inventario;
    }
}
