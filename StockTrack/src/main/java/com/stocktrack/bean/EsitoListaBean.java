package com.stocktrack.bean;

import java.util.ArrayList;
import java.util.List;

public class EsitoListaBean<T> {

    private boolean successo;
    private String messaggio;
    private List<T> elementi = new ArrayList<>();

    public EsitoListaBean() {
    }

    public EsitoListaBean(boolean successo, String messaggio, List<T> elementi) {
        this.successo = successo;
        this.messaggio = messaggio;
        setElementi(elementi);
    }

    public static <T> EsitoListaBean<T> success(String messaggio, List<T> elementi) {
        return new EsitoListaBean<>(true, messaggio, elementi);
    }

    public static <T> EsitoListaBean<T> failure(String messaggio) {
        return new EsitoListaBean<>(false, messaggio, List.of());
    }

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public List<T> getElementi() {
        return new ArrayList<>(elementi);
    }

    public void setElementi(List<T> elementi) {
        this.elementi = elementi == null ? new ArrayList<>() : new ArrayList<>(elementi);
    }
}
