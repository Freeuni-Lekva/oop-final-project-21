package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.QuizChallenge;
import java.util.List;
import java.util.Optional;

public interface QuizChallengeDAO {
    boolean createChallenge(QuizChallenge challenge);

    List<QuizChallenge> getChallengesReceivedByUser(int userId);

    List<QuizChallenge> getChallengesSentByUser(int userId);

    Optional<QuizChallenge> getChallengeById(Long challengeId);

    boolean updateChallengeStatus(Long challengeId, String status);

    boolean challengeExists(int challengerId, int challengedId, Long quizId);

    boolean deleteChallenge(Long challengeId);
}
