package com.stocktrack.persistence.memory;

import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.persistence.dao.MovimentoInventarioDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione del DAO che persiste MovimentoInventario in memoria per la modalità DEMO. Viene creata dalla factory e usata dai controller attraverso l'interfaccia DAO, preservando la separazione BCE.
 */
public class InMemoryMovimentoInventarioDAO implements MovimentoInventarioDAO {

    @Override
    public void save(MovimentoInventario movimento) {
        InMemoryDataStore.MOVIMENTI_INVENTARIO.put(movimento.getId(), movimento);
    }

    @Override
    public List<MovimentoInventario> findAll() {
        return new ArrayList<>(InMemoryDataStore.MOVIMENTI_INVENTARIO.values());
    }
}
