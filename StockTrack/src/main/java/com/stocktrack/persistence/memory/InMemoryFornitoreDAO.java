package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Fornitore;
import com.stocktrack.persistence.dao.FornitoreDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Fornitore in memoria per la modalità DEMO. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
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
