package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuantitaProdottoBeanTest {

    @Test
    void validateAccettaQuantitaZeroPerImpostazioneDiretta() {
        QuantitaProdottoBean bean = new QuantitaProdottoBean("PROD-1", 0);

        assertDoesNotThrow(bean::validate);
    }

    @Test
    void validateRifiutaProdottoNonSelezionato() {
        QuantitaProdottoBean bean = new QuantitaProdottoBean(" ", 1);

        assertThrows(InvalidInputException.class, bean::validate);
    }

    @Test
    void validateMovimentoRifiutaQuantitaZero() {
        QuantitaProdottoBean bean = new QuantitaProdottoBean("PROD-1", 0);

        assertThrows(InvalidInputException.class, bean::validateMovimento);
    }
}
