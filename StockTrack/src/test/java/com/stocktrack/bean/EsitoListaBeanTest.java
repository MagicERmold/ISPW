package com.stocktrack.bean;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EsitoListaBeanTest {

    @Test
    void successCopiaGliElementiRicevuti() {
        List<String> elementi = new ArrayList<>();
        elementi.add("primo");

        EsitoListaBean<String> esito = EsitoListaBean.success("Caricati", elementi);
        elementi.add("secondo");

        assertTrue(esito.isSuccesso());
        assertEquals("Caricati", esito.getMessaggio());
        assertEquals(List.of("primo"), esito.getElementi());
    }

    @Test
    void failureRestituisceListaVuota() {
        EsitoListaBean<String> esito = EsitoListaBean.failure("Errore");

        assertFalse(esito.isSuccesso());
        assertEquals("Errore", esito.getMessaggio());
        assertTrue(esito.getElementi().isEmpty());
    }
}
