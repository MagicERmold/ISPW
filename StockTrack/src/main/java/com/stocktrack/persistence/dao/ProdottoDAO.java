package com.stocktrack.persistence.dao;

import com.stocktrack.entity.Prodotto;
import com.stocktrack.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface ProdottoDAO {

    List<Prodotto> findAll() throws PersistenceException;

    Optional<Prodotto> findById(String id) throws PersistenceException;

    void save(Prodotto prodotto) throws PersistenceException;

    void update(Prodotto prodotto) throws PersistenceException;

    void deleteById(String id) throws PersistenceException;
}
