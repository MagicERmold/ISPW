package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodiceFornitoreBeanTest {

    @Test
    void validateAccettaCodiceFornitoreValido() {
        CodiceFornitoreBean bean = new CodiceFornitoreBean("APPLE-2026");

        assertDoesNotThrow(bean::validate);
        assertEquals("APPLE-2026", bean.getCodiceFornitore());
    }

    @Test
    void validateRifiutaCodiceFornitoreVuoto() {
        CodiceFornitoreBean bean = new CodiceFornitoreBean(" ");

        assertThrows(InvalidInputException.class, bean::validate);
    }
}
