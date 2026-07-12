package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PagamentoFallitoException;

/**
 * Adapter che implementa il gateway applicativo traducendo i bean BCE nel formato richiesto da PagamentoVisa. Isola il controller dalle API esterne concrete.
 */
public class PagamentoVisaAdapter implements PagamentoGateway {

    private final PagamentoVisaAdaptee visaApi;

    public PagamentoVisaAdapter() {
        this(new PagamentoVisaAdaptee());
    }

    public PagamentoVisaAdapter(PagamentoVisaAdaptee visaApi) {
        this.visaApi = visaApi;
    }

    @Override
    public EsitoPagamentoBean autorizzaPagamento(PagamentoBean pagamentoBean) throws PagamentoFallitoException {
        try {
            pagamentoBean.validate();
        } catch (InvalidInputException e) {
            throw new PagamentoFallitoException(e.getMessage(), e);
        }

        String authCode = visaApi.authorizeCard(pagamentoBean.getNumeroCarta(), pagamentoBean.getCvv(),
                pagamentoBean.getImporto());
        if (authCode == null || !visaApi.settleTransaction(authCode)) {
            throw new PagamentoFallitoException("Pagamento Visa rifiutato");
        }
        return new EsitoPagamentoBean(true, "Pagamento Visa autorizzato");
    }
}
