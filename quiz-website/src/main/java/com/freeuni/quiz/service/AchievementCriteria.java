package com.freeuni.quiz.service;

import java.sql.SQLException;

public interface AchievementCriteria {
    /**
     * Returns true if the achievement conditions are met for the user.
     */
    boolean isSatisfied(int userId) throws SQLException;

    /**
     * Returns the unique name/key of the achievement this criteria checks.
     */
    String getAchievementName();
}

