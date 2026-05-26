package com.stocktrack.persistence.memory;

import com.stocktrack.model.ActivityLog;
import com.stocktrack.persistence.dao.ActivityLogDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemoryActivityLogDAO implements ActivityLogDAO {
    private static final List<ActivityLog> activities = new ArrayList<>();

    @Override
    public void saveActivity(ActivityLog activityLog) {
        activities.add(activityLog);
    }

    @Override
    public List<ActivityLog> getRecentActivities(String groupId, int limit) {
        if (groupId == null || limit <= 0) {
            return new ArrayList<>();
        }

        return activities.stream()
                .filter(activity -> groupId.equals(activity.getGroupId()))
                .sorted(Comparator.comparing(ActivityLog::getTimestamp).reversed())
                .limit(limit)
                .toList();
    }
}
