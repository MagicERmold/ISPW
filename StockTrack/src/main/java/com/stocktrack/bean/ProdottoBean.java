package com.stocktrack.bean;

import com.stocktrack.common.AbstractProdottoData;
import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;

/**
 * Bean BCE che trasporta e valida i dati di un prodotto tra boundary, controller e gateway senza esporre l'entity.
 */
public class ProdottoBean extends AbstractProdottoData {

    public ProdottoBean() {
    }

    public ProdottoBean(String id, String nome, String categoria, int quantita, int sogliaMinima,
                       BigDecimal prezzoUnitario) {
        super(id, nome, categoria, quantita, sogliaMinima, prezzoUnitario);
    }

    public void validate() throws InvalidInputException {
        if (isBlank(getId())) {
            throw new InvalidInputException("Id prodotto obbligatorio");
        }
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome prodotto obbligatorio");
        }
        if (getQuantita() < 0) {
            throw new InvalidInputException("Quantita prodotto non valida");
        }
        if (getSogliaMinima() < 0) {
            throw new InvalidInputException("Soglia minima prodotto non valida");
        }
        if (getPrezzoUnitario() != null && getPrezzoUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Prezzo prodotto non valido");
        }
    }
}
