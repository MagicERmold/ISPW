package com.stocktrack.common;

import java.math.BigDecimal;

/**
 * Raccoglie i dati comuni alle rappresentazioni bean ed entity del prodotto, evitando duplicazione tra i livelli BCE.
 */
public abstract class AbstractProdottoData {

    private String id;
    private String nome;
    private String categoria;
    private int quantita;
    private int sogliaMinima;
    private BigDecimal prezzoUnitario = BigDecimal.ZERO;

    protected AbstractProdottoData() {
    }

    protected AbstractProdottoData(String id, String nome, String categoria, int quantita, int sogliaMinima,
                                  BigDecimal prezzoUnitario) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.quantita = quantita;
        this.sogliaMinima = sogliaMinima;
        this.prezzoUnitario = prezzoUnitario;
    }

    public boolean isDisponibile() {
        return quantita > 0;
    }

    public boolean isSottoSoglia() {
        return quantita <= sogliaMinima;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public int getSogliaMinima() {
        return sogliaMinima;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    protected boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
