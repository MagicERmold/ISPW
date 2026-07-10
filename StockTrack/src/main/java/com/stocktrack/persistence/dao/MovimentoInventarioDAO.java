package com.stocktrack.persistence.dao;

import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;

public interface MovimentoInventarioDAO {

    void save(MovimentoInventario movimento) throws PersistenceException;

    List<MovimentoInventario> findAll() throws PersistenceException;
}
