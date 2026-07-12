package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.exceptions.PagamentoFallitoException;

/**
 * Porta astratta usata dal controller per autorizzare pagamenti indipendentemente dal circuito scelto. Gli adapter Visa e PayPal ne forniscono le implementazioni.
 */
public interface PagamentoGateway {

    EsitoPagamentoBean autorizzaPagamento(PagamentoBean pagamentoBean) throws PagamentoFallitoException;
}
