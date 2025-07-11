package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.AchievementDAO;
import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.bean.UserAchievement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

public class AchievementService {
    private final AchievementDAO achievementDAO;
    private final List<AchievementCriteria> criteriaList;

    public AchievementService(DataSource dataSource) {
        this.achievementDAO = new AchievementDAO(dataSource);
        this.criteriaList = List.of(
                new AmateurAuthorCriteria(dataSource),
                new ProlificAuthorCriteria(dataSource),
                new ProdigiousAuthorCriteria(dataSource),
                new QuizMachineCriteria(dataSource),
                new QuizMasterCriteria(dataSource)
        );
    }

    public void checkAndAwardAchievements(int userId) throws SQLException {
        for (AchievementCriteria criteria : criteriaList) {
            if (criteria.isSatisfied(userId)) {
                String achievementName = criteria.getAchievementName();
                Long achievementId = achievementDAO.findByName(achievementName).getId();
                if (achievementId != null && !achievementDAO.userHasAchievement(userId, achievementId)) {
                    achievementDAO.awardAchievementToUser(
                            new UserAchievement(userId, new Achievement(achievementId), LocalDateTime.now())
                    );
                }
            }
        }
    }

    public List<UserAchievement> getUserAchievements(int userId) throws SQLException {
        return achievementDAO.getUserAchievements(userId);
    }
}
