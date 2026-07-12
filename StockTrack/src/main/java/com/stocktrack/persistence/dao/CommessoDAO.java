package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Commesso;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

/**
 * Contratto DAO della BCE per l'accesso persistente a Commesso. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface CommessoDAO {

    Optional<Commesso> findById(String id) throws PersistenceException;

    Optional<Commesso> findByEmail(String email) throws PersistenceException;

    List<Commesso> findAll() throws PersistenceException;

    void save(Commesso commesso) throws PersistenceException;
}
