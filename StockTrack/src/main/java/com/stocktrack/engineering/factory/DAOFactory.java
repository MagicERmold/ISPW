package com.stocktrack.engineering.factory;

import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.persistence.dao.UserDAO;
import com.stocktrack.persistence.fs.FileSystemStockDAO;
import com.stocktrack.persistence.memory.InMemoryStockDAO;
import com.stocktrack.persistence.memory.InMemoryUserDAO;

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
            return new FileSystemStockDAO();
        }else{
            throw new IllegalArgumentException("Tipo di persistenza non valido: " +  type);
        }
    }

    // --- NUOVO METODO AGGIUNTO ---
    public static UserDAO getUserDAO() throws IOException {
        String type = readPersistenceTypeFromConfig();

        // Nota: Per ora usiamo InMemory anche per la FULL version finché
        // non implementiamo il salvataggio utenti su file.
        if ("DEMO".equalsIgnoreCase(type) || "FULL".equalsIgnoreCase(type)) {
            return new InMemoryUserDAO();
        } else {
            throw new IllegalArgumentException("Tipo di persistenza non valido: " + type);
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
