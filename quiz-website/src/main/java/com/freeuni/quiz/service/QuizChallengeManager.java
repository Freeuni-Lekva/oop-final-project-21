package com.freeuni.quiz.service;

import java.sql.SQLException;

public class QuizChallengeManager {
    private final QuizChallengeService challengeService;
    private final FriendshipService friendshipService;

    public QuizChallengeManager(QuizChallengeService challengeService, FriendshipService friendshipService) {
        this.challengeService = challengeService;
        this.friendshipService = friendshipService;
    }

    public boolean sendChallengeToFriend(int challengerId, int friendId, Long quizId, String message) {
        try {
            if (!friendshipService.areFriends(challengerId, friendId)) {
                return false;
            }

            return challengeService.sendChallenge(challengerId, friendId, quizId, message);
        } catch (SQLException e) {
            throw new RuntimeException("Error checking friendship status", e);
        }
    }

    public String acceptChallengeAndGetQuizUrl(Long challengeId, UserService userService, QuizService quizService) {
        if (challengeService.acceptChallenge(challengeId)) {
            return challengeService.getChallengeById(challengeId, userService, quizService)
                    .map(challenge -> challenge.getQuizUrl())
                    .orElse(null);
        }
        return null;
    }
}