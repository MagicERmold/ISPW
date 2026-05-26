package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.ActivityLog;

import java.util.List;

public interface ActivityLogDAO {
    void saveActivity(ActivityLog activityLog) throws StorageException;
    List<ActivityLog> getRecentActivities(String groupId, int limit) throws StorageException;
}
