package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.Achievement;

import java.util.List;

public interface AchievementRepository {
    void saveAchievement(Achievement achievement);
    boolean exists(int userId, String type);
    List<Achievement> findByUserId(int userId);
}
