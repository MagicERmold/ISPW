package com.stocktrack.persistence.memory;

import com.stocktrack.entity.Prodotto;
import com.stocktrack.persistence.dao.ProdottoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProdottoDAO implements ProdottoDAO {

    @Override
    public List<Prodotto> findAll() {
        return new ArrayList<>(InMemoryDataStore.PRODOTTI.values());
    }

    @Override
    public Optional<Prodotto> findById(String id) {
        return Optional.ofNullable(InMemoryDataStore.PRODOTTI.get(id));
    }

    @Override
    public void save(Prodotto prodotto) {
        InMemoryDataStore.PRODOTTI.put(prodotto.getId(), prodotto);
    }

    @Override
    public void update(Prodotto prodotto) {
        InMemoryDataStore.PRODOTTI.put(prodotto.getId(), prodotto);
    }

    @Override
    public void deleteById(String id) {
        InMemoryDataStore.PRODOTTI.remove(id);
    }
}
