package com.stocktrack.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Legge la configurazione applicativa usata da avvio e factory per scegliere persistenza e interfaccia senza accoppiarle alla BCE.
 */
public class AppConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final String PERSISTENCE_TYPE = "persistence.type";

    private final Properties properties = new Properties();

    public AppConfig() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            properties.clear();
        }
    }

    public PersistenceMode getPersistenceMode() {
        String value = properties.getProperty(PERSISTENCE_TYPE, PersistenceMode.DEMO.name());
        return PersistenceMode.valueOf(value.trim().replace('-', '_').toUpperCase());
    }

}
