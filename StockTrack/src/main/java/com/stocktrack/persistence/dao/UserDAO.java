package com.stocktrack.persistence.dao;

import com.stocktrack.model.User;

import java.util.List;

public interface UserDAO {
    User findUserByUsername(String username);
    void saveUser(User user);
    void updateUser(User user);
    List<User> getAllUsers(); // NUOVO
    void deleteUser(String username); // NUOVO
}