package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface TitolareDAO {

    Optional<Titolare> findById(String id) throws PersistenceException;

    Optional<Titolare> findByEmail(String email) throws PersistenceException;

    List<Titolare> findAll() throws PersistenceException;

    void save(Titolare titolare) throws PersistenceException;
}
