package com.stocktrack.controller;

import com.stocktrack.bean.ActivityLogBean;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.factory.DAOFactory;
import com.stocktrack.model.ActivityLog;
import com.stocktrack.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogController {
    private static final int DEFAULT_LIMIT = 30;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void recordActivity(String actionType, String description) throws StorageException {
        User currentUser;
        try {
            currentUser = SessionGuard.requireUserWithGroup();
        } catch (StorageException e) {
            return;
        }

        ActivityLog activityLog = new ActivityLog(
                currentUser.getUsername(),
                currentUser.getGroupId(),
                actionType,
                description,
                LocalDateTime.now()
        );
        DAOFactory.getActivityLogDAO().saveActivity(activityLog);
    }

    public List<ActivityLogBean> getRecentActivities() throws StorageException {
        User currentUser = SessionGuard.requireUserWithGroup();

        List<ActivityLog> activities = DAOFactory.getActivityLogDAO()
                .getRecentActivities(currentUser.getGroupId(), DEFAULT_LIMIT);
        List<ActivityLogBean> beans = new ArrayList<>();
        for (ActivityLog activity : activities) {
            beans.add(new ActivityLogBean(
                    activity.getUsername(),
                    activity.getActionType(),
                    activity.getDescription(),
                    activity.getTimestamp().format(DISPLAY_FORMATTER)
            ));
        }
        return beans;
    }
}
