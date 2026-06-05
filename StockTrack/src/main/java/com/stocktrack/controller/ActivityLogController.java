package com.stocktrack.controller;

import com.stocktrack.bean.ActivityLogBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller applicativo per la registrazione e la consultazione delle attivita recenti.
 * Mantiene il log separato dalla logica dei singoli casi d'uso e lo espone alla Boundary
 * tramite bean dedicati.
 */
public class ActivityLogController {
    private static final int DEFAULT_LIMIT = 30;
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Registra un evento dell'utente corrente. Se non esiste una sessione valida,
     * l'evento viene ignorato per non bloccare il caso d'uso principale.
     *
     * @param actionType categoria sintetica dell'azione eseguita
     * @param description descrizione leggibile dell'evento
     * @throws StorageException se il log non puo essere salvato
     */
    public void recordActivity(String actionType, String description) throws StorageException {
        User currentUser;
        try {
            currentUser = SessionGuard.requireUserWithGroup();
        } catch (StorageException e) {
            return;
        }

        ActivityLog activityLog = new ActivityLog(
                currentUser.getUsername(),
                currentUser.getGroupId(),
                actionType,
                description,
                LocalDateTime.now(DEFAULT_ZONE)
        );
        DAOFactory.getActivityLogDAO().saveActivity(activityLog);
    }

    /**
     * Recupera le attivita recenti del gruppo dell'utente corrente.
     *
     * @return lista di bean pronti per la visualizzazione
     * @throws StorageException se la sessione non e valida o il log non puo essere letto
     */
    public List<ActivityLogBean> getRecentActivities() throws StorageException {
        User currentUser = SessionGuard.requireUserWithGroup();

        List<ActivityLog> activities = DAOFactory.getActivityLogDAO()
                .getRecentActivities(currentUser.getGroupId(), DEFAULT_LIMIT);
        List<ActivityLogBean> beans = new ArrayList<>();
        for (ActivityLog activity : activities) {
            beans.add(new ActivityLogBean(
                    activity.getUsername(),
                    activity.getActionType(),
                    activity.getDescription(),
                    activity.getTimestamp().format(DISPLAY_FORMATTER)
            ));
        }
        return beans;
    }
}
