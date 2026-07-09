package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.exceptions.PagamentoFallitoException;

public interface PagamentoGateway {

    EsitoPagamentoBean autorizzaPagamento(PagamentoBean pagamentoBean) throws PagamentoFallitoException;
}
