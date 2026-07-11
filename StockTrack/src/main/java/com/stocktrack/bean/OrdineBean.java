package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrdineBean {

    private String idOrdine;
    private FornitoreBean fornitore;
    private List<ProdottoBean> prodotti = new ArrayList<>();
    private BigDecimal totale = BigDecimal.ZERO;

    public OrdineBean() {
    }

    public OrdineBean(String idOrdine, FornitoreBean fornitore, List<ProdottoBean> prodotti, BigDecimal totale) {
        this.idOrdine = idOrdine;
        this.fornitore = fornitore;
        setProdotti(prodotti);
        this.totale = totale;
    }

    public void validate() throws InvalidInputException {
        if (fornitore == null) {
            throw new InvalidInputException("Fornitore ordine obbligatorio");
        }
        if (prodotti.isEmpty()) {
            throw new InvalidInputException("Ordine senza prodotti");
        }
        if (totale == null || totale.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Totale ordine non valido");
        }
    }

    public String getIdOrdine() {
        return idOrdine;
    }

    public FornitoreBean getFornitore() {
        return fornitore;
    }

    public void setFornitore(FornitoreBean fornitore) {
        this.fornitore = fornitore;
    }

    public List<ProdottoBean> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<ProdottoBean> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }

    public BigDecimal getTotale() {
        return totale;
    }

}
