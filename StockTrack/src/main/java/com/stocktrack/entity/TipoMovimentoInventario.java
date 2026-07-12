package com.stocktrack.entity;

/**
 * Tipo di dominio usato dall'entity movimento e dai controller per distinguere acquisti e vendite di inventario.
 */
public enum TipoMovimentoInventario {
    VENDITA,
    ACQUISTO_FORNITORE,
    ACQUISTO_ESTERNO
}
