package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.exceptions.FornitoreConnectionException;

import java.util.List;

/**
 * Porta astratta usata dai controller per comunicare con i fornitori. Permette di sostituire l'API concreta senza modificare il caso d'uso BCE.
 */
public interface FornitoreGateway {

    List<ProdottoBean> recuperaProdotti(FornitoreBean fornitoreBean) throws FornitoreConnectionException;

    void salvaProdotto(FornitoreBean fornitoreBean, ProdottoBean prodottoBean) throws FornitoreConnectionException;

    void notificaOrdine(OrdineBean ordineBean) throws FornitoreConnectionException;
}
