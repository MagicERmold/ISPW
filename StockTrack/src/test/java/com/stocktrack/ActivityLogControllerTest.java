package com.stocktrack;

import com.stocktrack.bean.ActivityLogBean;
import com.stocktrack.controller.ActivityLogController;
import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivityLogControllerTest {
    private ActivityLogController controller;

    @BeforeEach
    void setUp() {
        controller = new ActivityLogController();
        SessionManager.getInstance().login(new User("activityTester", "password", Role.ADMIN, "ACTIVITY_TEST_GROUP"));
    }

    @AfterEach
    void tearDown() {
        SessionManager.getInstance().logout();
    }

    @Test
    void testRecordAndReadRecentActivity() throws Exception {
        controller.recordActivity("TEST", "ha eseguito una attivita di prova");

        List<ActivityLogBean> activities = controller.getRecentActivities();

        assertFalse(activities.isEmpty(), "Il log deve contenere almeno l'attivita appena registrata.");
        assertTrue(activities.stream().anyMatch(activity ->
                activity.getUsername().equals("activityTester")
                        && activity.getActionType().equals("TEST")
                        && activity.getDescription().contains("prova")));
    }

    @Test
    void testReadRecentActivitiesWithoutLoginFails() {
        SessionManager.getInstance().logout();

        assertThrows(StorageException.class, () -> controller.getRecentActivities());
    }
}
