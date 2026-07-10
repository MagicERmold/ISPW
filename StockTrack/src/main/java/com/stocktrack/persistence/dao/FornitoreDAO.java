package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface FornitoreDAO {

    List<Fornitore> findAll() throws PersistenceException;

    Optional<Fornitore> findById(String id) throws PersistenceException;

    Optional<Fornitore> findByEmail(String email) throws PersistenceException;

    void save(Fornitore fornitore) throws PersistenceException;

    void deleteById(String id) throws PersistenceException;
}
