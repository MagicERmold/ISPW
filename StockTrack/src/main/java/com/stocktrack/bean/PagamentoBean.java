package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;

public class PagamentoBean {

    private String metodoPagamento;
    private String numeroCarta;
    private String cvv;
    private String emailAccount;
    private BigDecimal importo;
    private String valuta = "EUR";

    public PagamentoBean() {
    }

    public PagamentoBean(String metodoPagamento, String numeroCarta, String cvv, String emailAccount,
                         BigDecimal importo, String valuta) {
        this.metodoPagamento = metodoPagamento;
        this.numeroCarta = numeroCarta;
        this.cvv = cvv;
        this.emailAccount = emailAccount;
        this.importo = importo;
        this.valuta = valuta;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(metodoPagamento)) {
            throw new InvalidInputException("Metodo di pagamento obbligatorio");
        }
        if (importo == null || importo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Importo pagamento non valido");
        }
        if (isBlank(valuta)) {
            throw new InvalidInputException("Valuta pagamento obbligatoria");
        }
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public String getCvv() {
        return cvv;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public String getValuta() {
        return valuta;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
