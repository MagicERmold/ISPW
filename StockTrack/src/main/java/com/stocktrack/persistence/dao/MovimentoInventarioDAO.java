package com.stocktrack.persistence.dao;

import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

/**
 * Contratto DAO della BCE per l'accesso persistente a MovimentoInventario. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface MovimentoInventarioDAO {

    void save(MovimentoInventario movimento) throws PersistenceException;

    List<MovimentoInventario> findAll() throws PersistenceException;
}
