package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.User;

import java.sql.SQLException;
import java.util.*;

public interface UserDAO {
    
    boolean addUser(User user) throws SQLException;
    
    List<User> findUsers(String input) throws SQLException;
    
    User findById(int id) throws SQLException;
    
    User findByUsername(String username) throws SQLException;
    
    List<User> findAll() throws SQLException;
    
    boolean updateUser(User user) throws SQLException;
    
    boolean deleteUser(int id) throws SQLException;
    
    Map<Integer, UserDTO> findUsersByIds(Set<Integer> userIds) throws SQLException;
    
    boolean promoteToAdmin(int userId) throws SQLException;
    
    boolean demoteFromAdmin(int userId) throws SQLException;
    
    List<User> getAllAdmins() throws SQLException;
} 