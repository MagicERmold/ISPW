package com.stocktrack.engineering.factory;

import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.persistence.memory.InMemoryStockDAO;

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

    public static StockDAO getStockDAO() throws IOException {
        String type = readPersistenceTypeFromConfig();

        if("DEMO".equalsIgnoreCase(type)) {
            return new InMemoryStockDAO();
        }else if("FULL".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("Versione FULL non ancora implementata");
        }else{
            throw new IllegalArgumentException("Tipo di persistenza non valido: " +  type);
        }
    }

    private static String readPersistenceTypeFromConfig() throws IOException {
        Properties prop = new Properties();

        try(InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if(inputStream == null) {
                logger.warning(() -> "Impossibile trovare " + CONFIG_FILE + ". Defaulting to DEMO.");
                return "DEMO";
            }
            prop.load(inputStream);
            return prop.getProperty(PERSISTENCE_TYPE_KEY,  "DEMO");
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del file di configurazione", e);
            return "DEMO";
        }
    }
}
