package com.stocktrack.persistence.fs;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.persistence.dao.ActivityLogDAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class FileSystemActivityLogDAO implements ActivityLogDAO {
    private static final String CSV_FILE_NAME = "activities.csv";
    private static final Logger logger = Logger.getLogger(FileSystemActivityLogDAO.class.getName());
    private final File file = new File(System.getProperty("stocktrack.fs.activity.file", CSV_FILE_NAME));

    public FileSystemActivityLogDAO() {
        try {
            boolean isCreated = file.createNewFile();
            if (isCreated) {
                logger.info("Nuovo file log creato: " + CSV_FILE_NAME);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Errore: impossibile creare o accedere al file " + CSV_FILE_NAME, e);
        }
    }

    @Override
    public void saveActivity(ActivityLog activityLog) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(CsvCodec.join(
                    activityLog.getTimestamp().toString(),
                    activityLog.getUsername(),
                    activityLog.getGroupId(),
                    activityLog.getActionType(),
                    activityLog.getDescription()));
            writer.newLine();
        } catch (IOException e) {
            throw new StorageException("Errore salvataggio log attivita", e);
        }
    }

    @Override
    public List<ActivityLog> getRecentActivities(String groupId, int limit) throws StorageException {
        if (groupId == null || limit <= 0) {
            return new ArrayList<>();
        }

        try {
            return readAll().stream()
                    .filter(activity -> groupId.equals(activity.getGroupId()))
                    .sorted(Comparator.comparing(ActivityLog::getTimestamp).reversed())
                    .limit(limit)
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Errore lettura log attivita", e);
        }
    }

    private List<ActivityLog> readAll() throws IOException {
        List<ActivityLog> activities = new ArrayList<>();
        if (!file.exists()) {
            return activities;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = CsvCodec.split(line);
                if (parts.length >= 5) {
                    try {
                        activities.add(new ActivityLog(
                                parts[1],
                                parts[2],
                                parts[3],
                                parts[4],
                                LocalDateTime.parse(parts[0])
                        ));
                    } catch (RuntimeException e) {
                        throw new IOException("Riga log attivita malformata: " + line, e);
                    }
                }
            }
        }
        return activities;
    }
}
