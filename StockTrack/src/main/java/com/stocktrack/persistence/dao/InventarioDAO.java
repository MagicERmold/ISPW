package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Inventario;
import com.stocktrack.exceptions.PersistenceException;

/**
 * Contratto DAO della BCE per l'accesso persistente a Inventario. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface InventarioDAO {

    Inventario findInventario() throws PersistenceException;

    void save(Inventario inventario) throws PersistenceException;

    void update(Inventario inventario) throws PersistenceException;
}
