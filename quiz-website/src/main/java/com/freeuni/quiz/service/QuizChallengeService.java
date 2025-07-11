package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.QuizChallengeDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizChallenge;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.DAO.QuizChallengeDAO;
import com.freeuni.quiz.DAO.QuizCompletionDAO;
import com.freeuni.quiz.DAO.impl.QuizChallengeDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizCompletionDAOImpl;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizChallengeService {
    private final QuizChallengeDAO challengeRepository;
    private final QuizCompletionDAO completionRepository;

    public QuizChallengeService(DataSource dataSource) {
        this.challengeRepository = new QuizChallengeDAOImpl(dataSource);
        this.completionRepository = new QuizCompletionDAOImpl(dataSource);
    }

    public boolean sendChallenge(int challengerId, int challengedId, Long quizId, String message) {
        if (challengeRepository.challengeExists(challengerId, challengedId, quizId)) {
            return false;
        }

        QuizChallenge challenge = new QuizChallenge(challengerId, challengedId, quizId, message);
        return challengeRepository.createChallenge(challenge);
    }

    public List<QuizChallengeDTO> getReceivedChallenges(int userId, UserService userService, QuizService quizService) {
        List<QuizChallenge> challenges = challengeRepository.getChallengesReceivedByUser(userId);
        return convertToDTOs(challenges, userService, quizService);
    }

    public List<QuizChallengeDTO> getRecentReceivedChallenges(int userId, int limit, UserService userService, QuizService quizService) {
        List<QuizChallenge> challenges = challengeRepository.getChallengesReceivedByUser(userId);
        List<QuizChallenge> limitedChallenges = challenges.stream()
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
        return convertToDTOs(limitedChallenges, userService, quizService);
    }

    public List<QuizChallengeDTO> getSentChallenges(int userId, UserService userService, QuizService quizService) {
        List<QuizChallenge> challenges = challengeRepository.getChallengesSentByUser(userId);
        return convertToDTOs(challenges, userService, quizService);
    }

    public Optional<QuizChallengeDTO> getChallengeById(Long challengeId, UserService userService, QuizService quizService) {
        Optional<QuizChallenge> challenge = challengeRepository.getChallengeById(challengeId);
        if (challenge.isPresent()) {
            QuizChallengeDTO dto = convertToDTO(challenge.get(), userService, quizService);
            return dto != null ? Optional.of(dto) : Optional.empty();
        }
        return Optional.empty();
    }

    public boolean acceptChallenge(Long challengeId) {
        return challengeRepository.updateChallengeStatus(challengeId, "ACCEPTED");
    }

    public boolean declineChallenge(Long challengeId) {
        return challengeRepository.updateChallengeStatus(challengeId, "DECLINED");
    }

    public boolean completeChallenge(Long challengeId) {
        return challengeRepository.updateChallengeStatus(challengeId, "COMPLETED");
    }

    public boolean deleteChallenge(Long challengeId) {
        return challengeRepository.deleteChallenge(challengeId);
    }

    private List<QuizChallengeDTO> convertToDTOs(List<QuizChallenge> challenges, UserService userService, QuizService quizService) {
        List<QuizChallengeDTO> dtos = new ArrayList<>();
        for (QuizChallenge challenge : challenges) {
            QuizChallengeDTO dto = convertToDTO(challenge, userService, quizService);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    private QuizChallengeDTO convertToDTO(QuizChallenge challenge, UserService userService, QuizService quizService) {
        try {
            UserDTO challenger = userService.findById(challenge.getChallengerUserId());
            UserDTO challenged = userService.findById(challenge.getChallengedUserId());
            Optional<Quiz> quizOpt = quizService.getQuizById(challenge.getQuizId());

            if (challenger == null || challenged == null || quizOpt.isEmpty()) {
                return null;
            }

            Quiz quiz = quizOpt.get();

            QuizChallengeDTO dto = new QuizChallengeDTO(
                    challenge.getId(),
                    challenger,
                    challenged,
                    quiz,
                    challenge.getMessage(),
                    challenge.getCreatedAt(),
                    challenge.getStatus()
            );

            Optional<QuizCompletion> challengerScore = completionRepository.findUserCompletionForQuiz(
                    (long) challenge.getChallengerUserId(), challenge.getQuizId());
            Optional<QuizCompletion> challengedScore = completionRepository.findUserCompletionForQuiz(
                    (long) challenge.getChallengedUserId(), challenge.getQuizId());

            challengerScore.ifPresent(dto::setChallengerScore);
            challengedScore.ifPresent(dto::setChallengedScore);

            return dto;
        } catch (SQLException e) {
            System.err.println("Error converting challenge to DTO: " + e.getMessage());
            return null;
        }
    }
}