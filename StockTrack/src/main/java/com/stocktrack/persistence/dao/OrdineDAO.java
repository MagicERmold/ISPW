package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Ordine;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface OrdineDAO {

    void save(Ordine ordine) throws PersistenceException;

    Optional<Ordine> findById(String id) throws PersistenceException;

    List<Ordine> findAll() throws PersistenceException;
}
