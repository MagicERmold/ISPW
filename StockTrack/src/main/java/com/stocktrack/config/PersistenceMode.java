package com.stocktrack.config;

/**
 * Elenca le modalita di persistenza selezionabili dalla configurazione e consumate dall'Abstract Factory dei DAO.
 */
public enum PersistenceMode {
    DEMO,
    FULL_FS,
    FULL_DB
}
