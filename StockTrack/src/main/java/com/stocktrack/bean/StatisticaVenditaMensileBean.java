package com.stocktrack.bean;

import java.math.BigDecimal;

/**
 * Bean di risposta BCE con i dati statistici mensili calcolati dal controller e mostrati dalla view.
 */
public class StatisticaVenditaMensileBean {

    private String mese;
    private int quantitaVenduta;
    private int quantitaAcquistata;
    private BigDecimal incassoStimato = BigDecimal.ZERO;
    private BigDecimal spesaAcquisti = BigDecimal.ZERO;
    private String prodottoPiuVenduto;

    public StatisticaVenditaMensileBean() {
    }

    public StatisticaVenditaMensileBean(String mese, int quantitaVenduta, BigDecimal incassoStimato,
                                        String prodottoPiuVenduto) {
        this(mese, quantitaVenduta, 0, incassoStimato, BigDecimal.ZERO, prodottoPiuVenduto);
    }

    public StatisticaVenditaMensileBean(String mese, int quantitaVenduta, int quantitaAcquistata,
                                        BigDecimal incassoStimato, BigDecimal spesaAcquisti,
                                        String prodottoPiuVenduto) {
        this.mese = mese;
        this.quantitaVenduta = quantitaVenduta;
        this.quantitaAcquistata = quantitaAcquistata;
        this.incassoStimato = incassoStimato;
        this.spesaAcquisti = spesaAcquisti;
        this.prodottoPiuVenduto = prodottoPiuVenduto;
    }

    public String getMese() {
        return mese;
    }

    public int getQuantitaVenduta() {
        return quantitaVenduta;
    }

    public void setQuantitaVenduta(int quantitaVenduta) {
        this.quantitaVenduta = quantitaVenduta;
    }

    public int getQuantitaAcquistata() {
        return quantitaAcquistata;
    }

    public void setQuantitaAcquistata(int quantitaAcquistata) {
        this.quantitaAcquistata = quantitaAcquistata;
    }

    public BigDecimal getIncassoStimato() {
        return incassoStimato;
    }

    public void setIncassoStimato(BigDecimal incassoStimato) {
        this.incassoStimato = incassoStimato;
    }

    public BigDecimal getSpesaAcquisti() {
        return spesaAcquisti;
    }

    public void setSpesaAcquisti(BigDecimal spesaAcquisti) {
        this.spesaAcquisti = spesaAcquisti;
    }

    public String getProdottoPiuVenduto() {
        return prodottoPiuVenduto;
    }

    public void setProdottoPiuVenduto(String prodottoPiuVenduto) {
        this.prodottoPiuVenduto = prodottoPiuVenduto;
    }
}
