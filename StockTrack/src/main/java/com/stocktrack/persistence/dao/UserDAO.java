package com.stocktrack.persistence.dao;

import com.stocktrack.engineering.exception.StorageException;
import com.stocktrack.model.User;
import java.util.List;

public interface UserDAO {
    User findUserByUsername(String username) throws StorageException;
    void saveUser(User user) throws StorageException;
    void updateUser(User user) throws StorageException;
    List<User> getAllUsers() throws StorageException;
    void deleteUser(String username) throws StorageException;
}