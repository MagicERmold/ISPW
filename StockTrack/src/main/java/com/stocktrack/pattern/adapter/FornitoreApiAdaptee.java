package com.stocktrack.pattern.adapter;

import java.util.List;

public class FornitoreApiAdaptee {

    public List<String> fetchSupplierProducts(String endpoint, String supplierCode) {
        checkEndpoint(endpoint);
        return List.of(
                supplierCode + "-P1;Carta termica;Cancelleria;15;2.40",
                supplierCode + "-P2;Sacchetti biodegradabili;Materiali;0;4.10",
                supplierCode + "-P3;Detersivo superfici;Pulizia;25;5.90"
        );
    }

    public boolean sendOrderNotification(String endpoint, String orderPayload) {
        checkEndpoint(endpoint);
        return orderPayload != null && !orderPayload.isBlank() && !endpoint.contains("failnotify");
    }

    private void checkEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank() || endpoint.contains("offline")) {
            throw new IllegalStateException("API fornitore non raggiungibile");
        }
        if (endpoint.contains("timeout")) {
            throw new IllegalStateException("Timeout collegamento API fornitore");
        }
    }
}
