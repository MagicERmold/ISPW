package com.stocktrack.persistence.db;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.model.Role;
import com.stocktrack.model.Stock;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.ActivityLogDAO;
import com.stocktrack.persistence.dao.StockDAO;
import com.stocktrack.persistence.fs.FileSystemActivityLogDAO;
import com.stocktrack.persistence.fs.FileSystemStockDAO;
import com.stocktrack.persistence.fs.FileSystemUserDAO;
import com.stocktrack.persistence.memory.InMemoryActivityLogDAO;
import com.stocktrack.persistence.memory.InMemoryStockDAO;
import com.stocktrack.persistence.serial.SerializableActivityLogDAO;
import com.stocktrack.persistence.serial.SerializableStockDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockDAOConsistencyTest {
    private static final String GROUP_ID = "DAO_CONSISTENCY_GROUP";

    @TempDir
    Path tempDir;

    @AfterEach
    void clearProperties() {
        System.clearProperty("stocktrack.fs.stock.file");
        System.clearProperty("stocktrack.fs.user.file");
        System.clearProperty("stocktrack.fs.activity.file");
        System.clearProperty("stocktrack.serial.stock.file");
        System.clearProperty("stocktrack.serial.activity.file");
        System.clearProperty("stocktrack.jdbc.url");
    }

    @Test
    void allStockDaosFailWhenUpdatingMissingStock() throws Exception {
        for (StockDAO dao : createStockDaos()) {
            assertThrows(StorageException.class,
                    () -> dao.updateStockQuantity("Missing", 3, GROUP_ID),
                    dao.getClass().getSimpleName() + " deve segnalare prodotto mancante in update.");
        }
    }

    @Test
    void allStockDaosFailWhenDeletingMissingStock() throws Exception {
        for (StockDAO dao : createStockDaos()) {
            assertThrows(StorageException.class,
                    () -> dao.deleteStock("Missing", GROUP_ID),
                    dao.getClass().getSimpleName() + " deve segnalare prodotto mancante in delete.");
        }
    }

    @Test
    void fileSystemStockDaoKeepsCommaSeparatedFields() throws Exception {
        System.setProperty("stocktrack.fs.stock.file", tempDir.resolve("stocks.csv").toString());
        StockDAO dao = new FileSystemStockDAO();

        dao.saveStock(new Stock("Pane, integrale", 2, 5, GROUP_ID, "Cibo, dispensa"));

        List<Stock> stocks = dao.getAllStocks(GROUP_ID);

        assertEquals(1, stocks.size());
        assertEquals("Pane, integrale", stocks.getFirst().getName());
        assertEquals("Cibo, dispensa", stocks.getFirst().getCategory());
    }

    @Test
    void fileSystemUserDaoKeepsCommaSeparatedFields() throws Exception {
        System.setProperty("stocktrack.fs.user.file", tempDir.resolve("users.csv").toString());
        FileSystemUserDAO dao = new FileSystemUserDAO();

        dao.saveUser(new User("user,one", "pass,word", Role.ADMIN, "GROUP,CSV"));
        User found = dao.findUserByUsername("user,one");

        assertEquals("pass,word", found.getPassword());
        assertEquals("GROUP,CSV", found.getGroupId());
    }

    @Test
    void allActivityLogDaosReturnRecentActivitiesInDescendingOrder() throws Exception {
        for (ActivityLogDAO dao : createActivityLogDaos()) {
            dao.saveActivity(new ActivityLog("user1", GROUP_ID, "TEST", "first", LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0)));
            dao.saveActivity(new ActivityLog("user2", GROUP_ID, "TEST", "second", LocalDateTime.of(2026, Month.JANUARY, 1, 11, 0)));
            dao.saveActivity(new ActivityLog("user3", "OTHER_GROUP", "TEST", "ignored", LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0)));

            List<ActivityLog> activities = dao.getRecentActivities(GROUP_ID, 1);

            assertEquals(1, activities.size(), dao.getClass().getSimpleName() + " deve rispettare il limite richiesto.");
            assertEquals("second", activities.getFirst().getDescription(), dao.getClass().getSimpleName() + " deve ordinare dalla piu recente.");
        }
    }

    @Test
    void fileSystemActivityLogDaoKeepsCommaSeparatedDescriptions() throws Exception {
        System.setProperty("stocktrack.fs.activity.file", tempDir.resolve("activities.csv").toString());
        ActivityLogDAO dao = new FileSystemActivityLogDAO();

        dao.saveActivity(new ActivityLog("user,one", GROUP_ID, "TEST", "descrizione, con virgola", LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0)));

        List<ActivityLog> activities = dao.getRecentActivities(GROUP_ID, 10);

        assertEquals(1, activities.size());
        assertEquals("user,one", activities.getFirst().getUsername());
        assertEquals("descrizione, con virgola", activities.getFirst().getDescription());
    }

    @Test
    void allActivityLogDaosReturnEmptyListForInvalidLimit() throws Exception {
        for (ActivityLogDAO dao : createActivityLogDaos()) {
            dao.saveActivity(new ActivityLog("user1", GROUP_ID, "TEST", "stored", LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0)));

            assertTrue(dao.getRecentActivities(GROUP_ID, 0).isEmpty(), dao.getClass().getSimpleName() + " deve rispettare limit non positivo.");
            assertTrue(dao.getRecentActivities(null, 10).isEmpty(), dao.getClass().getSimpleName() + " deve ignorare groupId nullo.");
        }
    }

    private StockDAO[] createStockDaos() throws Exception {
        System.setProperty("stocktrack.fs.stock.file", tempDir.resolve("missing-stocks.csv").toString());
        System.setProperty("stocktrack.serial.stock.file", tempDir.resolve("missing-stocks.ser").toString());
        System.setProperty("stocktrack.jdbc.url", "jdbc:h2:mem:stock-dao-consistency;DB_CLOSE_DELAY=-1");
        resetDatabase();
        return new StockDAO[]{
                new InMemoryStockDAO(),
                new FileSystemStockDAO(),
                new SerializableStockDAO(),
                new DatabaseStockDAO()
        };
    }

    private ActivityLogDAO[] createActivityLogDaos() throws Exception {
        System.setProperty("stocktrack.fs.activity.file", tempDir.resolve("activities.csv").toString());
        System.setProperty("stocktrack.serial.activity.file", tempDir.resolve("activities.ser").toString());
        System.setProperty("stocktrack.jdbc.url", "jdbc:h2:mem:activity-dao-consistency;DB_CLOSE_DELAY=-1");
        resetDatabase();
        return new ActivityLogDAO[]{
                new InMemoryActivityLogDAO(),
                new FileSystemActivityLogDAO(),
                new SerializableActivityLogDAO(),
                new DatabaseActivityLogDAO()
        };
    }

    private void resetDatabase() throws Exception {
        DatabaseConnectionManager.initializeSchema();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM stocks");
            statement.executeUpdate("DELETE FROM users");
            statement.executeUpdate("DELETE FROM activity_logs");
        }
    }
}
