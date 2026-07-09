package com.stocktrack.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final String PERSISTENCE_TYPE = "persistence.type";
    private static final String VIEW_TYPE = "view.type";

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

    public ViewMode getViewMode() {
        String value = properties.getProperty(VIEW_TYPE, ViewMode.CLI.name());
        return ViewMode.valueOf(value.trim().toUpperCase());
    }
}
