package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.bean.UserAchievement;

import java.sql.SQLException;
import java.util.List;

public interface AchievementDAO {
    
    boolean addAchievementDefinition(Achievement achievement) throws SQLException;
    
    boolean awardAchievementToUser(UserAchievement userAchievement) throws SQLException;
    
    boolean userHasAchievement(int userId, long achievementId) throws SQLException;
    
    List<UserAchievement> getUserAchievements(int userId) throws SQLException;
    
    List<Achievement> getAllAchievements() throws SQLException;
    
    Achievement findByName(String name) throws SQLException;
} 