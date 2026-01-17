package com.stocktrack.persistence.dao;

import com.stocktrack.model.User;

public interface UserDAO {
    User findUserByUsername(String username);
    void saveUser(User user);
    void updateUser(User user); // METODO MANCANTE AGGIUNTO
}