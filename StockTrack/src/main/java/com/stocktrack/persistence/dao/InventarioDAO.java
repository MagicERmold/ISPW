package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Inventario;
import com.stocktrack.exceptions.PersistenceException;

public interface InventarioDAO {

    Inventario findInventario() throws PersistenceException;

    void save(Inventario inventario) throws PersistenceException;

    void update(Inventario inventario) throws PersistenceException;
}
