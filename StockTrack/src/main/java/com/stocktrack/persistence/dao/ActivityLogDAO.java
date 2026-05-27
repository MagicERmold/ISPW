package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;

import java.util.List;

/**
 * Contratto di persistenza per il log delle attivita del gruppo.
 */
public interface ActivityLogDAO {
    /**
     * Salva una nuova attivita.
     */
    void saveActivity(ActivityLog activityLog) throws StorageException;

    /**
     * Recupera le attivita piu recenti per il gruppo indicato.
     */
    List<ActivityLog> getRecentActivities(String groupId, int limit) throws StorageException;
}
