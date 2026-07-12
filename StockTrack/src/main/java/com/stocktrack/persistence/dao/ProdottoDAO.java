package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

/**
 * Contratto DAO della BCE per l'accesso persistente a Prodotto. Viene usato dai controller tramite l'Abstract Factory, così la logica applicativa non dipende da memoria, file system o database.
 */
public interface ProdottoDAO {

    List<Prodotto> findAll() throws PersistenceException;

    Optional<Prodotto> findById(String id) throws PersistenceException;

    void save(Prodotto prodotto) throws PersistenceException;

    void update(Prodotto prodotto) throws PersistenceException;

    void deleteById(String id) throws PersistenceException;
}
