package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Message;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageDAO {
    
    Message getMessageById(Long id) throws SQLException;
    
    Long sendMessage(int senderId, int receiverId, String content) throws SQLException;
    
    List<Message> getRecentMessages(int user1, int user2) throws SQLException;
    
    List<Message> getMessagesBefore(int user1, int user2, LocalDateTime beforeTime, Long beforeId) throws SQLException;
    
    List<Message> getLatestConversations(int userId) throws SQLException;
    
    int getPageSize();
}