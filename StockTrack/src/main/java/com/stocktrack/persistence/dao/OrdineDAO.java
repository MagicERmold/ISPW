package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

/**
 * Contratto DAO della BCE per l'accesso persistente a Ordine. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface OrdineDAO {

    void save(Ordine ordine) throws PersistenceException;

    Optional<Ordine> findById(String id) throws PersistenceException;

    List<Ordine> findAll() throws PersistenceException;
}
