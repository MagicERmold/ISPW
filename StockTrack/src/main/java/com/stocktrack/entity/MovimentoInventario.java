package com.stocktrack.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Entity BCE che registra un acquisto o una vendita di magazzino; viene creata dai controller e salvata dai DAO.
 */
public class MovimentoInventario {

    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    private String id;
    private String idProdotto;
    private String nomeProdotto;
    private TipoMovimentoInventario tipo;
    private int quantita;
    private BigDecimal valoreUnitario = BigDecimal.ZERO;
    private LocalDateTime dataMovimento;
    private String origine;

    public MovimentoInventario() {
    }

    public MovimentoInventario(String id, String idProdotto, String nomeProdotto, TipoMovimentoInventario tipo,
                               int quantita, BigDecimal valoreUnitario, String origine) {
        this.id = id;
        this.idProdotto = idProdotto;
        this.nomeProdotto = nomeProdotto;
        this.tipo = tipo;
        this.quantita = quantita;
        this.valoreUnitario = valoreUnitario;
        this.origine = origine;
        this.dataMovimento = LocalDateTime.now(APPLICATION_ZONE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(String idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public TipoMovimentoInventario getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoInventario tipo) {
        this.tipo = tipo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getValoreUnitario() {
        return valoreUnitario;
    }

    public void setValoreUnitario(BigDecimal valoreUnitario) {
        this.valoreUnitario = valoreUnitario;
    }

    public LocalDateTime getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(LocalDateTime dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }
}
