package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

/**
 * Contratto DAO della BCE per l'accesso persistente a Titolare. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface TitolareDAO {

    Optional<Titolare> findById(String id) throws PersistenceException;

    Optional<Titolare> findByEmail(String email) throws PersistenceException;

    List<Titolare> findAll() throws PersistenceException;

    void save(Titolare titolare) throws PersistenceException;
}
