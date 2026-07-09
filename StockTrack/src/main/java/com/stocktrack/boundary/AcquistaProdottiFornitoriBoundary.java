package com.stocktrack.boundary;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoPagamentoBean;
import com.stocktrack.bean.FornitoreBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.OrdineBean;
import com.stocktrack.bean.PagamentoBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.RegistrazioneBean;
import com.stocktrack.controller.AcquistaProdottiFornitoriController;
import com.stocktrack.controller.LoginController;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.FornitoreConnectionException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.exceptions.ProdottoNonDisponibileException;

import java.util.List;

public class AcquistaProdottiFornitoriBoundary {

    public ProfiloUtenteBean login(LoginBean loginBean) {
        try {
            return new LoginController().login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public ProfiloUtenteBean registra(RegistrazioneBean registrazioneBean) {
        try {
            return new LoginController().registra(registrazioneBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public List<FornitoreBean> recuperaFornitori() {
        try {
            return new AcquistaProdottiFornitoriController().recuperaFornitori();
        } catch (PersistenceException e) {
            return List.of();
        }
    }

    public List<ProdottoBean> recuperaProdotti(FornitoreBean fornitoreBean) {
        try {
            fornitoreBean.validate();
            return new AcquistaProdottiFornitoriController().recuperaProdotti(fornitoreBean);
        } catch (FornitoreConnectionException | InvalidInputException e) {
            return List.of();
        }
    }

    public CarrelloBean configuraCarrello(List<ProdottoBean> prodottiSelezionati) {
        try {
            return new AcquistaProdottiFornitoriController().configuraCarrello(prodottiSelezionati);
        } catch (InvalidInputException | ProdottoNonDisponibileException e) {
            return new CarrelloBean();
        }
    }

    public EsitoPagamentoBean effettuaPagamento(PagamentoBean pagamentoBean) {
        try {
            pagamentoBean.validate();
            return new AcquistaProdottiFornitoriController().elaboraPagamento(pagamentoBean);
        } catch (InvalidInputException e) {
            return new EsitoPagamentoBean(false, null, e.getMessage());
        }
    }

    public EsitoOrdineBean confermaOrdine(OrdineBean ordineBean) {
        try {
            ordineBean.validate();
            return new AcquistaProdottiFornitoriController().acquistaProdottiDaFornitore(ordineBean);
        } catch (InvalidInputException e) {
            return new EsitoOrdineBean(false, ordineBean.getIdOrdine(), e.getMessage());
        }
    }
}
