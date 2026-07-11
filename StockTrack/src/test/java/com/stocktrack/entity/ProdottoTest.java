package com.stocktrack.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProdottoTest {

    @Test
    void aumentaQuantitaSommaSoloValoriPositivi() {
        Prodotto prodotto = new Prodotto("PROD-1", "Telefono", "Smartphone", 5, 2, BigDecimal.TEN);

        prodotto.aumentaQuantita(3);
        prodotto.aumentaQuantita(0);

        assertEquals(8, prodotto.getQuantita());
    }

    @Test
    void riduciQuantitaNonScendeSottoZero() {
        Prodotto prodotto = new Prodotto("PROD-1", "Telefono", "Smartphone", 5, 2, BigDecimal.TEN);

        prodotto.riduciQuantita(3);
        prodotto.riduciQuantita(10);

        assertEquals(2, prodotto.getQuantita());
    }
}
