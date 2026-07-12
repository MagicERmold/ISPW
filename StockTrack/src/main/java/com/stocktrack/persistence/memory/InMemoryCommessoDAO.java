package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Commesso;
import com.stocktrack.persistence.dao.CommessoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO che persiste Commesso in memoria per la modalità DEMO. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class InMemoryCommessoDAO implements CommessoDAO {

    @Override
    public Optional<Commesso> findById(String id) {
        return Optional.ofNullable(InMemoryDataStore.COMMESSI.get(id));
    }

    @Override
    public Optional<Commesso> findByEmail(String email) {
        return InMemoryDataStore.COMMESSI.values().stream()
                .filter(commesso -> commesso.getEmail() != null && commesso.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Commesso> findAll() {
        return new ArrayList<>(InMemoryDataStore.COMMESSI.values());
    }

    @Override
    public void save(Commesso commesso) {
        InMemoryDataStore.COMMESSI.put(commesso.getId(), commesso);
    }
}
