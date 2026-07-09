package com.stocktrack.entity;

import java.math.BigDecimal;

public class Prodotto {

    private String id;
    private String nome;
    private String categoria;
    private int quantita;
    private int sogliaMinima;
    private BigDecimal prezzoUnitario = BigDecimal.ZERO;

    public Prodotto() {
    }

    public Prodotto(String id, String nome, String categoria, int quantita, int sogliaMinima,
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

    public void aumentaQuantita(int quantitaDaAggiungere) {
        if (quantitaDaAggiungere > 0) {
            quantita += quantitaDaAggiungere;
        }
    }

    public void riduciQuantita(int quantitaDaRimuovere) {
        if (quantitaDaRimuovere > 0 && quantitaDaRimuovere <= quantita) {
            quantita -= quantitaDaRimuovere;
        }
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

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public void setSogliaMinima(int sogliaMinima) {
        this.sogliaMinima = sogliaMinima;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(BigDecimal prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }
}
