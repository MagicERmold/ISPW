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
    public Optional<Fornitore> findByEmail(String email) {
        return InMemoryDataStore.FORNITORI.values().stream()
                .filter(fornitore -> fornitore.getEmail() != null && fornitore.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void save(Fornitore fornitore) {
        InMemoryDataStore.FORNITORI.put(fornitore.getId(), fornitore);
    }

    @Override
    public void deleteById(String id) {
        InMemoryDataStore.FORNITORI.remove(id);
    }
}
