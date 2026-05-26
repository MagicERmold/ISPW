package com.stocktrack.persistence.db;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Stock;
import com.stocktrack.persistence.dao.StockDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseStockDAO implements StockDAO {
    public DatabaseStockDAO() throws StorageException {
        DatabaseConnectionManager.initializeSchema();
    }

    @Override
    public void saveStock(Stock stock) throws StorageException {
        String sql = """
                INSERT INTO stocks(name, quantity, threshold_value, group_id, category)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, stock.getName());
            statement.setInt(2, stock.getQuantity());
            statement.setInt(3, stock.getThreshold());
            statement.setString(4, stock.getGroupId());
            statement.setString(5, stock.getCategory());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Errore salvataggio prodotto su DBMS", e);
        }
    }

    @Override
    public List<Stock> getAllStocks(String groupUid) throws StorageException {
        if (groupUid == null) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT name, quantity, threshold_value, group_id, category
                FROM stocks
                WHERE group_id = ?
                ORDER BY category, name
                """;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupUid);
            return readStocks(statement);
        } catch (SQLException e) {
            throw new StorageException("Errore lettura prodotti da DBMS", e);
        }
    }

    @Override
    public void updateStockQuantity(String stockName, int newQuantity, String groupUid) throws StorageException {
        String sql = "UPDATE stocks SET quantity = ? WHERE LOWER(name) = LOWER(?) AND group_id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, stockName);
            statement.setString(3, groupUid);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new StorageException("Prodotto non trovato nel DBMS");
            }
        } catch (SQLException e) {
            throw new StorageException("Errore aggiornamento quantità su DBMS", e);
        }
    }

    @Override
    public void deleteStock(String stockName, String groupUid) throws StorageException {
        String sql = "DELETE FROM stocks WHERE LOWER(name) = LOWER(?) AND group_id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, stockName);
            statement.setString(2, groupUid);
            int deletedRows = statement.executeUpdate();
            if (deletedRows == 0) {
                throw new StorageException("Prodotto non trovato nel DBMS");
            }
        } catch (SQLException e) {
            throw new StorageException("Errore eliminazione prodotto da DBMS", e);
        }
    }

    @Override
    public List<String> getAllCategories(String groupId) throws StorageException {
        if (groupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT DISTINCT category FROM stocks WHERE group_id = ? ORDER BY category";
        List<String> categories = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String category = resultSet.getString("category");
                    if (category != null && !category.isBlank()) {
                        categories.add(category);
                    }
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Errore lettura categorie da DBMS", e);
        }
        return categories;
    }

    @Override
    public List<Stock> getStocksByCategory(String groupId, String category) throws StorageException {
        if (groupId == null) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT name, quantity, threshold_value, group_id, category
                FROM stocks
                WHERE group_id = ? AND LOWER(category) = LOWER(?)
                ORDER BY name
                """;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupId);
            statement.setString(2, category);
            return readStocks(statement);
        } catch (SQLException e) {
            throw new StorageException("Errore filtro prodotti da DBMS", e);
        }
    }

    private static List<Stock> readStocks(PreparedStatement statement) throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                stocks.add(new Stock(
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("threshold_value"),
                        resultSet.getString("group_id"),
                        resultSet.getString("category")
                ));
            }
        }
        return stocks;
    }
}
