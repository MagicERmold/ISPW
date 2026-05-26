package com.stocktrack.persistence.serial;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.persistence.dao.ActivityLogDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SerializableActivityLogDAO implements ActivityLogDAO {
    private static final String FILE_NAME = "activities.ser";
    private final File file;

    public SerializableActivityLogDAO() throws StorageException {
        this.file = new File(System.getProperty("stocktrack.serial.activity.file", FILE_NAME));
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }

    @Override
    public void saveActivity(ActivityLog activityLog) throws StorageException {
        List<ActivityLog> activities = loadAll();
        activities.add(activityLog);
        saveAll(activities);
    }

    @Override
    public List<ActivityLog> getRecentActivities(String groupId, int limit) throws StorageException {
        if (groupId == null || limit <= 0) {
            return new ArrayList<>();
        }

        return loadAll().stream()
                .filter(activity -> groupId.equals(activity.getGroupId()))
                .sorted(Comparator.comparing(ActivityLog::getTimestamp).reversed())
                .limit(limit)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<ActivityLog> loadAll() throws StorageException {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ActivityLog>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Errore lettura log attivita serializzato", e);
        }
    }

    private void saveAll(List<ActivityLog> activities) throws StorageException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(activities);
        } catch (IOException e) {
            throw new StorageException("Errore scrittura log attivita serializzato", e);
        }
    }
}
