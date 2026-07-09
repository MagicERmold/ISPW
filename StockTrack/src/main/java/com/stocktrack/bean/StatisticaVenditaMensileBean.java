package com.stocktrack.bean;

import java.math.BigDecimal;

public class StatisticaVenditaMensileBean {

    private String mese;
    private int quantitaVenduta;
    private BigDecimal incassoStimato = BigDecimal.ZERO;
    private String prodottoPiuVenduto;

    public StatisticaVenditaMensileBean() {
    }

    public StatisticaVenditaMensileBean(String mese, int quantitaVenduta, BigDecimal incassoStimato,
                                        String prodottoPiuVenduto) {
        this.mese = mese;
        this.quantitaVenduta = quantitaVenduta;
        this.incassoStimato = incassoStimato;
        this.prodottoPiuVenduto = prodottoPiuVenduto;
    }

    public String getMese() {
        return mese;
    }

    public void setMese(String mese) {
        this.mese = mese;
    }

    public int getQuantitaVenduta() {
        return quantitaVenduta;
    }

    public void setQuantitaVenduta(int quantitaVenduta) {
        this.quantitaVenduta = quantitaVenduta;
    }

    public BigDecimal getIncassoStimato() {
        return incassoStimato;
    }

    public void setIncassoStimato(BigDecimal incassoStimato) {
        this.incassoStimato = incassoStimato;
    }

    public String getProdottoPiuVenduto() {
        return prodottoPiuVenduto;
    }

    public void setProdottoPiuVenduto(String prodottoPiuVenduto) {
        this.prodottoPiuVenduto = prodottoPiuVenduto;
    }
}
