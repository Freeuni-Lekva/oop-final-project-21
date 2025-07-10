package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Achievement;
import com.freeuni.quiz.repository.AchievementRepository;
import com.freeuni.quiz.repository.QuizCompletionRepository;
import com.freeuni.quiz.repository.QuizRepository;
import java.util.List;
import java.time.LocalDateTime;

public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final QuizRepository quizRepository;
    private final QuizCompletionRepository quizCompletionRepository;

    public AchievementService(AchievementRepository achievementRepo,
                              QuizRepository quizRepo,
                              QuizCompletionRepository completionRepo) {
        this.achievementRepository = achievementRepo;
        this.quizRepository = quizRepo;
        this.quizCompletionRepository = completionRepo;
    }

    public void checkAchievements(int userId) {
        int createdCount = quizRepository.findByCreator((long)userId, 0, Integer.MAX_VALUE).size();
        int takenCount = quizCompletionRepository.findByParticipant((long) userId).size();

        if (createdCount >= 1 && !achievementRepository.exists(userId, "AMATEUR_AUTHOR")) {
            award(userId, "AMATEUR_AUTHOR");
        }
        if (createdCount >= 5 && !achievementRepository.exists(userId, "PROLIFIC_AUTHOR")) {
            award(userId, "PROLIFIC_AUTHOR");
        }
        if (createdCount >= 10 && !achievementRepository.exists(userId, "PRODIGIOUS_AUTHOR")) {
            award(userId, "PRODIGIOUS_AUTHOR");
        }
        if (takenCount >= 10 && !achievementRepository.exists(userId, "QUIZ_MACHINE")) {
            award(userId, "QUIZ_MACHINE");
        }
    }

    public List<Achievement> getAchievementsByUser(int userId) {
        return achievementRepository.findByUserId(userId);
    }

    public void award(int userId, String type) {
        Achievement achievement = new Achievement(userId, type, LocalDateTime.now());
        achievementRepository.saveAchievement(achievement);
    }
}
