package com.stocktrack.persistence.db;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.persistence.dao.ActivityLogDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivityLogDAO implements ActivityLogDAO {
    public DatabaseActivityLogDAO() throws StorageException {
        DatabaseConnectionManager.initializeSchema();
    }

    @Override
    public void saveActivity(ActivityLog activityLog) throws StorageException {
        String sql = """
                INSERT INTO activity_logs(username, group_id, action_type, description, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, activityLog.getUsername());
            statement.setString(2, activityLog.getGroupId());
            statement.setString(3, activityLog.getActionType());
            statement.setString(4, activityLog.getDescription());
            statement.setString(5, activityLog.getTimestamp().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Errore salvataggio log attivita su DBMS", e);
        }
    }

    @Override
    public List<ActivityLog> getRecentActivities(String groupId, int limit) throws StorageException {
        if (groupId == null || limit <= 0) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT username, group_id, action_type, description, created_at
                FROM activity_logs
                WHERE group_id = ?
                ORDER BY created_at DESC
                LIMIT ?
                """;
        List<ActivityLog> activities = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupId);
            statement.setInt(2, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(new ActivityLog(
                            resultSet.getString("username"),
                            resultSet.getString("group_id"),
                            resultSet.getString("action_type"),
                            resultSet.getString("description"),
                            LocalDateTime.parse(resultSet.getString("created_at"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Errore lettura log attivita da DBMS", e);
        }
        return activities;
    }
}
