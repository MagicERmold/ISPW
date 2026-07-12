package com.stocktrack.pattern.adapter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Componente esterno simulato con l'interfaccia specifica di PagamentoPaypal. È usato esclusivamente dal relativo adapter, così la BCE non dipende dal servizio concreto.
 */
public class PagamentoPaypalAdaptee {

    public String createPayment(BigDecimal amount, String currency, String accountEmail) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || isBlank(currency) || isBlank(accountEmail)) {
            return null;
        }
        if (accountEmail.endsWith("@fail.local")) {
            return null;
        }
        return "PAYPAL-PAY-" + UUID.randomUUID();
    }

    public boolean capturePayment(String paymentId) {
        return paymentId != null && paymentId.startsWith("PAYPAL-PAY-");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
