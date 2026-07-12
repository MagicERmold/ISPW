package com.stocktrack.pattern.adapter;

import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Adapter che traduce i bean BCE nelle chiamate richieste dall'API simulata del fornitore e converte gli errori tecnici in eccezioni applicative. È usato dai controller tramite FornitoreGateway.
 */
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
    public void salvaProdotto(FornitoreBean fornitoreBean, ProdottoBean prodottoBean)
            throws FornitoreConnectionException {
        try {
            fornitoreBean.validate();
            prodottoBean.validate();
            fornitoreApi.saveSupplierProduct(fornitoreBean.getApiEndpoint(), fornitoreBean.getId(),
                    toRawProduct(prodottoBean));
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
            for (ProdottoBean prodotto : ordineBean.getProdotti()) {
                fornitoreApi.decreaseSupplierProductStock(fornitore.getId(), prodotto.getId(),
                        prodotto.getQuantita());
            }
        } catch (InvalidInputException | IllegalStateException e) {
            throw new FornitoreConnectionException(e.getMessage(), e);
        }
    }

    public Optional<FornitoreBean> recuperaFornitoreDaCodice(String codiceFornitore) {
        return fornitoreApi.findSupplierByCode(codiceFornitore)
                .map(this::toFornitoreBean);
    }

    private ProdottoBean toProdottoBean(String rawProduct) {
        String[] columns = rawProduct.split(";", -1);
        return new ProdottoBean(columns[0], columns[1], columns[2], Integer.parseInt(columns[3]), 0,
                new BigDecimal(columns[4]));
    }

    private FornitoreBean toFornitoreBean(Fornitore fornitore) {
        return new FornitoreBean(fornitore.getId(), fornitore.getNome(), fornitore.getEmail(),
                fornitore.getApiEndpoint(), fornitore.isDisponibile());
    }

    private String toRawProduct(ProdottoBean prodottoBean) {
        BigDecimal prezzo = prodottoBean.getPrezzoUnitario() == null ? BigDecimal.ZERO
                : prodottoBean.getPrezzoUnitario();
        return String.join(";", prodottoBean.getId(), prodottoBean.getNome(), safe(prodottoBean.getCategoria()),
                Integer.toString(prodottoBean.getQuantita()), prezzo.toPlainString());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String buildPayload(OrdineBean ordineBean) {
        return ordineBean.getIdOrdine() + ";" + ordineBean.getTotale() + ";" + ordineBean.getProdotti().size();
    }
}
