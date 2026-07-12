package com.stocktrack.pattern.singleton;

import com.stocktrack.bean.RuoloUtente;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton che gestisce sessioni concorrenti indicizzate e lega la sessione corrente al thread. Viene consultato dai controller per autenticazione e autorizzazioni senza inserire stato utente nei controller.
 */
public class SessionManagerSingleton {

    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ThreadLocal<String> currentSessionId = new ThreadLocal<>();

    private SessionManagerSingleton() {
    }

    public static SessionManagerSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public void createSession(String idUtente, RuoloUtente ruolo) {
        if (idUtente == null || idUtente.isBlank()) {
            throw new IllegalArgumentException("Id utente obbligatorio");
        }
        if (ruolo == null) {
            throw new IllegalArgumentException("Ruolo utente obbligatorio");
        }

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, idUtente, ruolo, LocalDateTime.now(APPLICATION_ZONE));
        sessions.put(sessionId, session);
        currentSessionId.set(sessionId);
    }

    public Optional<Session> getCurrentSession() {
        String sessionId = currentSessionId.get();
        if (sessionId == null) {
            return Optional.empty();
        }
        return getSession(sessionId);
    }

    public Optional<Session> getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null || !session.isActive()) {
            return Optional.empty();
        }
        session.touch();
        return Optional.of(session);
    }

    public void logoutCurrentSession() {
        String sessionId = currentSessionId.get();
        if (sessionId != null) {
            logout(sessionId);
        }
        currentSessionId.remove();
    }

    public void logout(String sessionId) {
        Session session = sessions.remove(sessionId);
        if (session != null) {
            session.invalidate();
        }
    }

    private static class Holder {

        private static final SessionManagerSingleton INSTANCE = new SessionManagerSingleton();
    }
}
