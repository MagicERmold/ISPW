package com.stocktrack.persistence.db;

import com.stocktrack.model.Role;
import com.stocktrack.model.Stock;
import com.stocktrack.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseDAOTest {
    private static final String GROUP_ID = "DB_TEST_GROUP";

    @BeforeAll
    static void configureInMemoryDatabase() {
        System.setProperty("stocktrack.jdbc.url", "jdbc:h2:mem:stocktrack-test;DB_CLOSE_DELAY=-1");
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        DatabaseConnectionManager.initializeSchema();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM stocks");
            statement.executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void databaseUserDaoSupportsCrudOperations() throws Exception {
        DatabaseUserDAO userDAO = new DatabaseUserDAO();
        User user = new User("dbUser", "password123", Role.ADMIN, GROUP_ID);

        userDAO.saveUser(user);
        User found = userDAO.findUserByUsername("dbUser");

        assertNotNull(found);
        assertEquals(Role.ADMIN, found.getRole());
        assertEquals(GROUP_ID, found.getGroupId());

        found.setRole(Role.USER);
        found.setGroupId(null);
        userDAO.updateUser(found);

        User updated = userDAO.findUserByUsername("dbUser");
        assertNotNull(updated);
        assertEquals(Role.USER, updated.getRole());
        assertNull(updated.getGroupId());

        userDAO.deleteUser("dbUser");
        assertNull(userDAO.findUserByUsername("dbUser"));
    }

    @Test
    void databaseStockDaoSupportsInventoryQueries() throws Exception {
        DatabaseStockDAO stockDAO = new DatabaseStockDAO();
        stockDAO.saveStock(new Stock("Pasta", 3, 5, GROUP_ID, "Cibo"));
        stockDAO.saveStock(new Stock("Detersivo", 8, 2, GROUP_ID, "Casa"));

        List<Stock> stocks = stockDAO.getAllStocks(GROUP_ID);
        assertEquals(2, stocks.size());

        stockDAO.updateStockQuantity("Pasta", 9, GROUP_ID);
        Stock updated = stockDAO.getStocksByCategory(GROUP_ID, "Cibo").getFirst();
        assertEquals(9, updated.getQuantity());

        List<String> categories = stockDAO.getAllCategories(GROUP_ID);
        assertTrue(categories.contains("Cibo"));
        assertTrue(categories.contains("Casa"));

        stockDAO.deleteStock("Detersivo", GROUP_ID);
        assertEquals(1, stockDAO.getAllStocks(GROUP_ID).size());
    }
}
