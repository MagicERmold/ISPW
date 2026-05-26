package com.stocktrack.persistence.db;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import com.stocktrack.persistence.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUserDAO implements UserDAO {
    public DatabaseUserDAO() throws StorageException {
        DatabaseConnectionManager.initializeSchema();
    }

    @Override
    public User findUserByUsername(String username) throws StorageException {
        String sql = "SELECT username, password, role, group_id FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Errore ricerca utente su DBMS", e);
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws StorageException {
        String sql = "INSERT INTO users(username, password, role, group_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindUser(statement, user);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Errore salvataggio utente su DBMS", e);
        }
    }

    @Override
    public void updateUser(User user) throws StorageException {
        String sql = "UPDATE users SET password = ?, role = ?, group_id = ? WHERE username = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getRole().name());
            statement.setString(3, user.getGroupId());
            statement.setString(4, user.getUsername());
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                saveUser(user);
            }
        } catch (SQLException e) {
            throw new StorageException("Errore aggiornamento utente su DBMS", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws StorageException {
        String sql = "SELECT username, password, role, group_id FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            throw new StorageException("Errore lettura utenti da DBMS", e);
        }
        return users;
    }

    @Override
    public void deleteUser(String username) throws StorageException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Errore eliminazione utente da DBMS", e);
        }
    }

    private static void bindUser(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getRole().name());
        statement.setString(4, user.getGroupId());
    }

    private static User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("username"),
                resultSet.getString("password"),
                Role.valueOf(resultSet.getString("role")),
                resultSet.getString("group_id")
        );
    }
}
