package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarrelloBeanTest {

    @Test
    void costruttoreImpostaCarrelloConfigurato() {
        ProdottoBean prodotto = new ProdottoBean("PROD-1", "Telefono", "Smartphone", 2, 1, BigDecimal.TEN);
        CarrelloBean carrello = new CarrelloBean(List.of(prodotto), BigDecimal.valueOf(20));

        assertTrue(carrello.isSuccesso());
        assertEquals("Carrello configurato", carrello.getMessaggio());
        assertEquals(BigDecimal.valueOf(20), carrello.getTotaleStimato());
        assertDoesNotThrow(carrello::validate);
    }

    @Test
    void setProdottiCopiaLaListaRicevuta() {
        ProdottoBean prodotto = new ProdottoBean("PROD-1", "Telefono", "Smartphone", 1, 1, BigDecimal.TEN);
        List<ProdottoBean> prodotti = new ArrayList<>();
        prodotti.add(prodotto);

        CarrelloBean carrello = new CarrelloBean();
        carrello.setProdotti(prodotti);
        prodotti.clear();

        assertEquals(1, carrello.getProdotti().size());
    }

    @Test
    void validateRifiutaCarrelloVuoto() {
        CarrelloBean carrello = new CarrelloBean();

        assertThrows(InvalidInputException.class, carrello::validate);
    }
}
