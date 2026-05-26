package com.stocktrack.engineering.factory;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.persistence.dao.UserDAO;
import com.stocktrack.persistence.dao.ActivityLogDAO;
import com.stocktrack.persistence.db.DatabaseActivityLogDAO;
import com.stocktrack.persistence.db.DatabaseStockDAO;
import com.stocktrack.persistence.db.DatabaseUserDAO;
import com.stocktrack.persistence.fs.FileSystemActivityLogDAO;
import com.stocktrack.persistence.fs.FileSystemStockDAO;
import com.stocktrack.persistence.fs.FileSystemUserDAO;
import com.stocktrack.persistence.memory.InMemoryActivityLogDAO;
import com.stocktrack.persistence.memory.InMemoryStockDAO;
import com.stocktrack.persistence.memory.InMemoryUserDAO;
import com.stocktrack.persistence.serial.SerializableActivityLogDAO;
import com.stocktrack.persistence.serial.SerializableStockDAO;
import com.stocktrack.persistence.serial.SerializableUserDAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOFactory {
    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static final String PERSISTENCE_TYPE_KEY = "persistence.type";
    private static final String DEMO = "DEMO";
    private static final String FULL_FS = "FULL-FS";
    private static final String FULL_SR = "FULL-SR";
    private static final String FULL_DB = "FULL-DB";

    private DAOFactory() {}

    public static StockDAO getStockDAO() throws StorageException {
        String type = readPersistenceTypeFromConfig();

        return switch (type.toUpperCase()) {
            case DEMO -> new InMemoryStockDAO();
            case FULL_FS -> new FileSystemStockDAO();
            case FULL_SR -> new SerializableStockDAO();
            case FULL_DB -> new DatabaseStockDAO();
            default -> throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
        };
    }

    public static UserDAO getUserDAO() throws StorageException {
        String type = readPersistenceTypeFromConfig();

        return switch (type.toUpperCase()) {
            case DEMO -> new InMemoryUserDAO();
            case FULL_FS -> new FileSystemUserDAO();
            case FULL_SR -> new SerializableUserDAO();
            case FULL_DB -> new DatabaseUserDAO();
            default -> throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
        };
    }

    public static ActivityLogDAO getActivityLogDAO() throws StorageException {
        String type = readPersistenceTypeFromConfig();

        return switch (type.toUpperCase()) {
            case DEMO -> new InMemoryActivityLogDAO();
            case FULL_FS -> new FileSystemActivityLogDAO();
            case FULL_SR -> new SerializableActivityLogDAO();
            case FULL_DB -> new DatabaseActivityLogDAO();
            default -> throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
        };
    }

    private static String readPersistenceTypeFromConfig()  {
        Properties prop = new Properties();

        try(InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if(inputStream == null) {
                logger.warning(() -> "Impossibile trovare " + CONFIG_FILE + ". Defaulting to DEMO.");
                return DEMO;
            }
            prop.load(inputStream);
            return prop.getProperty(PERSISTENCE_TYPE_KEY, DEMO);
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del file di configurazione", e);
            return DEMO;
        }
    }
}
