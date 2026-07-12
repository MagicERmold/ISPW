package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Titolare;
import com.stocktrack.persistence.dao.TitolareDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Titolare in memoria per la modalità DEMO. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class InMemoryTitolareDAO implements TitolareDAO {

    @Override
    public Optional<Titolare> findById(String id) {
        return Optional.ofNullable(InMemoryDataStore.TITOLARI.get(id));
    }

    @Override
    public Optional<Titolare> findByEmail(String email) {
        return InMemoryDataStore.TITOLARI.values().stream()
                .filter(titolare -> titolare.getEmail() != null && titolare.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Titolare> findAll() {
        return new ArrayList<>(InMemoryDataStore.TITOLARI.values());
    }

    @Override
    public void save(Titolare titolare) {
        InMemoryDataStore.TITOLARI.put(titolare.getId(), titolare);
    }
}
