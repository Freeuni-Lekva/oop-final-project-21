package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Friendship;

import java.sql.SQLException;
import java.util.List;

public interface FriendshipDAO {
    
    boolean addFriendship(Friendship friendship) throws SQLException;
    
    Friendship findById(int id) throws SQLException;
    
    List<Friendship> findAll() throws SQLException;
    
    boolean deleteFriendship(int id) throws SQLException;
    
    boolean updateFriendship(Friendship friendship) throws SQLException;
    
    boolean exists(int userId1, int userId2) throws SQLException;
    
    List<Integer> findFriendIdsByUserId(int userId) throws SQLException;
    
    Integer findFriendshipId(int userId1, int userId2) throws SQLException;
}