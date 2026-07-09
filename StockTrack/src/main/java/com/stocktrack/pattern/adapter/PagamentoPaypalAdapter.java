package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PagamentoFallitoException;

public class PagamentoPaypalAdapter implements PagamentoGateway {

    private final PagamentoPaypalAdaptee paypalApi;

    public PagamentoPaypalAdapter() {
        this(new PagamentoPaypalAdaptee());
    }

    public PagamentoPaypalAdapter(PagamentoPaypalAdaptee paypalApi) {
        this.paypalApi = paypalApi;
    }

    @Override
    public EsitoPagamentoBean autorizzaPagamento(PagamentoBean pagamentoBean) throws PagamentoFallitoException {
        try {
            pagamentoBean.validate();
        } catch (InvalidInputException e) {
            throw new PagamentoFallitoException(e.getMessage(), e);
        }

        String paymentId = paypalApi.createPayment(pagamentoBean.getImporto(), pagamentoBean.getValuta(),
                pagamentoBean.getEmailAccount());
        if (paymentId == null || !paypalApi.capturePayment(paymentId)) {
            throw new PagamentoFallitoException("Pagamento PayPal rifiutato");
        }
        return new EsitoPagamentoBean(true, paymentId, "Pagamento PayPal autorizzato");
    }
}
