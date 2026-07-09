package com.stocktrack.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fattura {

    private String numero;
    private String idOrdine;
    private BigDecimal importo;
    private LocalDate dataEmissione;

    public Fattura() {
    }

    public Fattura(String numero, String idOrdine, BigDecimal importo, LocalDate dataEmissione) {
        this.numero = numero;
        this.idOrdine = idOrdine;
        this.importo = importo;
        this.dataEmissione = dataEmissione;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(String idOrdine) {
        this.idOrdine = idOrdine;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(LocalDate dataEmissione) {
        this.dataEmissione = dataEmissione;
    }
}
