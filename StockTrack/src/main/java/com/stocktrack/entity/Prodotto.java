package com.stocktrack.entity;

import com.stocktrack.common.AbstractProdottoData;

import java.math.BigDecimal;

public class Prodotto extends AbstractProdottoData {

    public Prodotto() {
    }

    public Prodotto(String id, String nome, String categoria, int quantita, int sogliaMinima,
                    BigDecimal prezzoUnitario) {
        super(id, nome, categoria, quantita, sogliaMinima, prezzoUnitario);
    }

    public synchronized void aumentaQuantita(int quantitaDaAggiungere) {
        if (quantitaDaAggiungere > 0) {
            setQuantita(getQuantita() + quantitaDaAggiungere);
        }
    }

    public synchronized void riduciQuantita(int quantitaDaRimuovere) {
        if (quantitaDaRimuovere > 0 && quantitaDaRimuovere <= getQuantita()) {
            setQuantita(getQuantita() - quantitaDaRimuovere);
        }
    }
}
