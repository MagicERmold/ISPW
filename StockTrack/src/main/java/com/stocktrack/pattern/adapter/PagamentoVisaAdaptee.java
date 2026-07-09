package com.stocktrack.pattern.adapter;

import java.math.BigDecimal;
import java.util.UUID;

public class PagamentoVisaAdaptee {

    public String authorizeCard(String cardNumber, String cvv, BigDecimal amount) {
        if (isBlank(cardNumber) || isBlank(cvv) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (cardNumber.endsWith("0000")) {
            return null;
        }
        return "VISA-AUTH-" + UUID.randomUUID();
    }

    public boolean settleTransaction(String authCode) {
        return authCode != null && authCode.startsWith("VISA-AUTH-");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
