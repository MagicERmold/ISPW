package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Commesso;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface CommessoDAO {

    Optional<Commesso> findById(String id) throws PersistenceException;

    Optional<Commesso> findByEmail(String email) throws PersistenceException;

    List<Commesso> findAll() throws PersistenceException;

    void save(Commesso commesso) throws PersistenceException;
}
