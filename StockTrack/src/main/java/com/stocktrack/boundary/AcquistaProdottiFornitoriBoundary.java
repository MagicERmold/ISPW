package com.stocktrack.boundary;

import com.stocktrack.bean.CarrelloBean;
import com.stocktrack.bean.EsitoListaBean;
import com.stocktrack.bean.EsitoOrdineBean;
import com.stocktrack.bean.EsitoOperazioneBean;
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
        LoginController controller = new LoginController();
        try {
            validateLogin(loginBean);
            return controller.login(loginBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public ProfiloUtenteBean registra(RegistrazioneBean registrazioneBean) {
        LoginController controller = new LoginController();
        try {
            validateRegistrazione(registrazioneBean);
            return controller.registra(registrazioneBean);
        } catch (AutenticazioneException | InvalidInputException | PersistenceException e) {
            return null;
        }
    }

    public EsitoOperazioneBean logout() {
        LoginController controller = new LoginController();
        return controller.logout();
    }

    public List<FornitoreBean> recuperaFornitori() {
        return recuperaFornitoriConEsito().getElementi();
    }

    public EsitoListaBean<FornitoreBean> recuperaFornitoriConEsito() {
        AcquistaProdottiFornitoriController controller = new AcquistaProdottiFornitoriController();
        try {
            List<FornitoreBean> fornitori = controller.recuperaFornitori();
            String messaggio = fornitori.isEmpty() ? "Nessun fornitore disponibile" : "Fornitori caricati";
            return EsitoListaBean.success(messaggio, fornitori);
        } catch (PersistenceException e) {
            return EsitoListaBean.failure("Errore caricamento fornitori: " + e.getMessage());
        }
    }

    public List<ProdottoBean> recuperaProdotti(FornitoreBean fornitoreBean) {
        return recuperaProdottiConEsito(fornitoreBean).getElementi();
    }

    public EsitoListaBean<ProdottoBean> recuperaProdottiConEsito(FornitoreBean fornitoreBean) {
        AcquistaProdottiFornitoriController controller = new AcquistaProdottiFornitoriController();
        try {
            validateFornitore(fornitoreBean);
            List<ProdottoBean> prodotti = controller.recuperaProdotti(fornitoreBean);
            String messaggio = prodotti.isEmpty() ? "Nessun prodotto disponibile" : "Prodotti caricati";
            return EsitoListaBean.success(messaggio, prodotti);
        } catch (FornitoreConnectionException | InvalidInputException e) {
            return EsitoListaBean.failure(e.getMessage());
        }
    }

    public CarrelloBean configuraCarrello(List<ProdottoBean> prodottiSelezionati) {
        AcquistaProdottiFornitoriController controller = new AcquistaProdottiFornitoriController();
        try {
            validateProdotti(prodottiSelezionati);
            return controller.configuraCarrello(prodottiSelezionati);
        } catch (InvalidInputException | ProdottoNonDisponibileException e) {
            return new CarrelloBean(false, e.getMessage());
        }
    }

    public EsitoPagamentoBean effettuaPagamento(PagamentoBean pagamentoBean) {
        AcquistaProdottiFornitoriController controller = new AcquistaProdottiFornitoriController();
        try {
            if (pagamentoBean == null) {
                throw new InvalidInputException("Dati pagamento obbligatori");
            }
            pagamentoBean.validate();
            return controller.elaboraPagamento(pagamentoBean);
        } catch (InvalidInputException e) {
            return new EsitoPagamentoBean(false, null, e.getMessage());
        }
    }

    public EsitoOrdineBean confermaOrdine(OrdineBean ordineBean) {
        AcquistaProdottiFornitoriController controller = new AcquistaProdottiFornitoriController();
        try {
            validateOrdine(ordineBean);
            return controller.acquistaProdottiDaFornitore(ordineBean);
        } catch (InvalidInputException e) {
            String idOrdine = ordineBean == null ? null : ordineBean.getIdOrdine();
            return new EsitoOrdineBean(false, idOrdine, e.getMessage());
        }
    }

    private void validateOrdine(OrdineBean ordineBean) throws InvalidInputException {
        if (ordineBean == null) {
            throw new InvalidInputException("Ordine obbligatorio");
        }
        ordineBean.validate();
        ordineBean.getFornitore().validate();
        validateProdotti(ordineBean.getProdotti());
    }

    private void validateProdotti(List<ProdottoBean> prodotti) throws InvalidInputException {
        if (prodotti == null || prodotti.isEmpty()) {
            throw new InvalidInputException("Selezionare almeno un prodotto");
        }
        for (ProdottoBean prodottoBean : prodotti) {
            if (prodottoBean == null) {
                throw new InvalidInputException("Prodotto selezionato non valido");
            }
            prodottoBean.validate();
        }
    }

    private void validateLogin(LoginBean loginBean) throws InvalidInputException {
        if (loginBean == null) {
            throw new InvalidInputException("Credenziali obbligatorie");
        }
        loginBean.validate();
    }

    private void validateRegistrazione(RegistrazioneBean registrazioneBean) throws InvalidInputException {
        if (registrazioneBean == null) {
            throw new InvalidInputException("Dati registrazione obbligatori");
        }
        registrazioneBean.validate();
    }

    private void validateFornitore(FornitoreBean fornitoreBean) throws InvalidInputException {
        if (fornitoreBean == null) {
            throw new InvalidInputException("Selezionare un fornitore");
        }
        fornitoreBean.validate();
    }
}
