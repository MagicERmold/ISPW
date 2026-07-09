package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.persistence.dao.FornitoreDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryFornitoreDAO implements FornitoreDAO {

    @Override
    public List<Fornitore> findAll() {
        return new ArrayList<>(InMemoryDataStore.FORNITORI.values());
    }

    @Override
    public Optional<Fornitore> findById(String id) {
        return Optional.ofNullable(InMemoryDataStore.FORNITORI.get(id));
    }

    @Override
    public void save(Fornitore fornitore) {
        InMemoryDataStore.FORNITORI.put(fornitore.getId(), fornitore);
    }
}
