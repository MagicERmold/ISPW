package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.util.List;

public class FornitoreApiAdapter implements FornitoreGateway {

    private final FornitoreApiAdaptee fornitoreApi;

    public FornitoreApiAdapter() {
        this(new FornitoreApiAdaptee());
    }

    public FornitoreApiAdapter(FornitoreApiAdaptee fornitoreApi) {
        this.fornitoreApi = fornitoreApi;
    }

    @Override
    public List<ProdottoBean> recuperaProdotti(FornitoreBean fornitoreBean) throws FornitoreConnectionException {
        try {
            fornitoreBean.validate();
            return fornitoreApi.fetchSupplierProducts(fornitoreBean.getApiEndpoint(), fornitoreBean.getId()).stream()
                    .map(this::toProdottoBean)
                    .toList();
        } catch (InvalidInputException | IllegalStateException e) {
            throw new FornitoreConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void notificaOrdine(OrdineBean ordineBean) throws FornitoreConnectionException {
        try {
            ordineBean.validate();
            FornitoreBean fornitore = ordineBean.getFornitore();
            boolean notified = fornitoreApi.sendOrderNotification(fornitore.getApiEndpoint(), buildPayload(ordineBean));
            if (!notified) {
                throw new FornitoreConnectionException("Notifica ordine al fornitore fallita");
            }
        } catch (InvalidInputException | IllegalStateException e) {
            throw new FornitoreConnectionException(e.getMessage(), e);
        }
    }

    private ProdottoBean toProdottoBean(String rawProduct) {
        String[] columns = rawProduct.split(";", -1);
        return new ProdottoBean(columns[0], columns[1], columns[2], Integer.parseInt(columns[3]), 0,
                new BigDecimal(columns[4]));
    }

    private String buildPayload(OrdineBean ordineBean) {
        return ordineBean.getIdOrdine() + ";" + ordineBean.getTotale() + ";" + ordineBean.getProdotti().size();
    }
}
