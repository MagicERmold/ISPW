package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Ordine;
import com.stocktrack.persistence.dao.OrdineDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryOrdineDAO implements OrdineDAO {

    @Override
    public void save(Ordine ordine) {
        InMemoryDataStore.ORDINI.put(ordine.getId(), ordine);
    }

    @Override
    public Optional<Ordine> findById(String id) {
        return Optional.ofNullable(InMemoryDataStore.ORDINI.get(id));
    }

    @Override
    public List<Ordine> findAll() {
        return new ArrayList<>(InMemoryDataStore.ORDINI.values());
    }
}
