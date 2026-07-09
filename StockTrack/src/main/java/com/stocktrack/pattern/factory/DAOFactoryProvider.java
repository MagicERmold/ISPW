package com.stocktrack.pattern.factory;

import com.stocktrack.config.AppConfig;
import com.stocktrack.config.PersistenceMode;

public class DAOFactoryProvider {

    private DAOFactoryProvider() {
    }

    public static DAOFactory getFactory() {
        return getFactory(new AppConfig());
    }

    public static DAOFactory getFactory(AppConfig appConfig) {
        PersistenceMode persistenceMode = appConfig.getPersistenceMode();
        return switch (persistenceMode) {
            case DEMO -> new MemoryDAOFactory();
            case FULL_FS -> new FileSystemDAOFactory();
            case FULL_DB -> new JDBCDAOFactory();
        };
    }
}
