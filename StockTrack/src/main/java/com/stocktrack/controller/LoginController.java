package com.stocktrack.controller;

import com.stocktrack.bean.EsitoOperazioneBean;
import com.stocktrack.bean.LoginBean;
import com.stocktrack.bean.ProfiloUtenteBean;
import com.stocktrack.bean.RegistrazioneBean;
import com.stocktrack.bean.RuoloUtente;
import com.stocktrack.entity.Commesso;
import com.stocktrack.entity.Fornitore;
import com.stocktrack.entity.Titolare;
import com.stocktrack.exceptions.AutenticazioneException;
import com.stocktrack.exceptions.InvalidInputException;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.factory.DAOFactory;
import com.stocktrack.pattern.factory.DAOFactoryProvider;
import com.stocktrack.pattern.singleton.Session;
import com.stocktrack.pattern.singleton.SessionManagerSingleton;
import com.stocktrack.security.PasswordHasher;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class LoginController {

    public ProfiloUtenteBean login(LoginBean loginBean)
            throws AutenticazioneException, PersistenceException {
        DAOFactory daoFactory = DAOFactoryProvider.getFactory();
        Optional<Titolare> titolare = daoFactory.getTitolareDAO().findByEmail(loginBean.getUsername());
        if (titolare.isPresent()) {
            verificaPassword(loginBean.getPassword(), titolare.get().getPasswordHash());
            return creaProfiloTitolare(titolare.get());
        }

        Optional<Commesso> commesso = daoFactory.getCommessoDAO().findByEmail(loginBean.getUsername());
        if (commesso.isPresent()) {
            verificaPassword(loginBean.getPassword(), commesso.get().getPasswordHash());
            return creaProfiloCommesso(commesso.get());
        }

        Optional<Fornitore> fornitore = daoFactory.getFornitoreDAO().findByEmail(loginBean.getUsername());
        if (fornitore.isPresent()) {
            verificaPassword(loginBean.getPassword(), fornitore.get().getPasswordHash());
            return creaProfiloFornitore(fornitore.get());
        }

        throw new AutenticazioneException("Credenziali non valide");
    }

    public ProfiloUtenteBean registra(RegistrazioneBean registrazioneBean)
            throws AutenticazioneException, PersistenceException {
        DAOFactory daoFactory = DAOFactoryProvider.getFactory();
        if (daoFactory.getTitolareDAO().findByEmail(registrazioneBean.getEmail()).isPresent()
                || daoFactory.getCommessoDAO().findByEmail(registrazioneBean.getEmail()).isPresent()
                || daoFactory.getFornitoreDAO().findByEmail(registrazioneBean.getEmail()).isPresent()) {
            throw new AutenticazioneException("Email gia registrata");
        }

        if (RuoloUtente.COMMESSO.equals(registrazioneBean.getRuolo())) {
            Commesso commesso = new Commesso("COM-" + UUID.randomUUID(), registrazioneBean.getNome(),
                    registrazioneBean.getCognome(), registrazioneBean.getEmail(),
                    PasswordHasher.hash(registrazioneBean.getPassword()));
            daoFactory.getCommessoDAO().save(commesso);
            return creaProfiloCommesso(commesso);
        }

        if (RuoloUtente.FORNITORE.equals(registrazioneBean.getRuolo())) {
            String supplierCode = registrazioneBean.getNome().trim().toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9]+", "-");
            Fornitore fornitore = new Fornitore("FOR-" + UUID.randomUUID(), registrazioneBean.getNome(),
                    registrazioneBean.getEmail(), "simulated://fornitori/registrati/" + supplierCode, true,
                    PasswordHasher.hash(registrazioneBean.getPassword()));
            daoFactory.getFornitoreDAO().save(fornitore);
            return creaProfiloFornitore(fornitore);
        }

        Titolare titolare = new Titolare("TIT-" + UUID.randomUUID(), registrazioneBean.getNome(),
                registrazioneBean.getCognome(), registrazioneBean.getEmail(),
                PasswordHasher.hash(registrazioneBean.getPassword()));
        daoFactory.getTitolareDAO().save(titolare);
        return creaProfiloTitolare(titolare);
    }

    public EsitoOperazioneBean logout() {
        SessionManagerSingleton.getInstance().logoutCurrentSession();
        return new EsitoOperazioneBean(true, "Logout effettuato");
    }

    private ProfiloUtenteBean creaProfiloTitolare(Titolare titolare) {
        Session session = SessionManagerSingleton.getInstance().createSession(titolare.getId(), RuoloUtente.TITOLARE);
        return new ProfiloUtenteBean(titolare.getNome(), RuoloUtente.TITOLARE
        );
    }

    private ProfiloUtenteBean creaProfiloCommesso(Commesso commesso) {
        Session session = SessionManagerSingleton.getInstance().createSession(commesso.getId(), RuoloUtente.COMMESSO);
        return new ProfiloUtenteBean(commesso.getNome(), RuoloUtente.COMMESSO
        );
    }

    private ProfiloUtenteBean creaProfiloFornitore(Fornitore fornitore) {
        Session session = SessionManagerSingleton.getInstance().createSession(fornitore.getId(), RuoloUtente.FORNITORE);
        return new ProfiloUtenteBean(fornitore.getNome(), RuoloUtente.FORNITORE
        );
    }

    private void verificaPassword(String password, String expectedHash) throws AutenticazioneException {
        if (expectedHash == null || expectedHash.isBlank()
                || !PasswordHasher.hash(password).equals(expectedHash)) {
            throw new AutenticazioneException("Credenziali non valide");
        }
    }
}
