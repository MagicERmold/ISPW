package com.stocktrack.engineering.factory;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.persistence.dao.UserDAO;
import com.stocktrack.persistence.fs.FileSystemStockDAO;
import com.stocktrack.persistence.fs.FileSystemUserDAO;
import com.stocktrack.persistence.memory.InMemoryStockDAO;
import com.stocktrack.persistence.memory.InMemoryUserDAO;
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

    private DAOFactory() {}

    public static StockDAO getStockDAO() throws StorageException {
        String type = readPersistenceTypeFromConfig();

        return switch (type.toUpperCase()) {
            case "DEMO" -> new InMemoryStockDAO();
            case "FULL-FS" -> new FileSystemStockDAO();
            case "FULL-SR" -> new SerializableStockDAO();
            default -> throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
        };
    }

    public static UserDAO getUserDAO() throws IOException {
        String type = readPersistenceTypeFromConfig();

        return switch (type.toUpperCase()) {
            case "DEMO" -> new InMemoryUserDAO();
            case "FULL-FS" -> new FileSystemUserDAO();
            case "FULL-SR" -> new SerializableUserDAO();
            default -> throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
        };
    }

    private static String readPersistenceTypeFromConfig()  {
        Properties prop = new Properties();

        try(InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if(inputStream == null) {
                logger.warning(() -> "Impossibile trovare " + CONFIG_FILE + ". Defaulting to DEMO.");
                return "DEMO";
            }
            prop.load(inputStream);
            return prop.getProperty(PERSISTENCE_TYPE_KEY, "DEMO");
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del file di configurazione", e);
            return "DEMO";
        }
    }
}
