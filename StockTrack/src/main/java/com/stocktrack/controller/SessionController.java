package com.stocktrack.controller;

import com.stocktrack.bean.UserProfileBean;
import com.stocktrack.engineering.singleton.SessionManager;
import com.stocktrack.model.Role;
import com.stocktrack.model.User;

public class SessionController {
    public UserProfileBean getCurrentUserProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return new UserProfileBean(
                currentUser.getUsername(),
                currentUser.getRole().name(),
                currentUser.getGroupId(),
                currentUser.getRole() == Role.ADMIN
        );
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}
