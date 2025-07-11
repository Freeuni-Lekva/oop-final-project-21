package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.QuizChallengeDTO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizChallenge;
import com.freeuni.quiz.repository.QuizChallengeRepository;
import com.freeuni.quiz.repository.impl.QuizChallengeRepositoryImpl;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizChallengeService {
    private final QuizChallengeRepository challengeRepository;

    public QuizChallengeService(DataSource dataSource) {
        this.challengeRepository = new QuizChallengeRepositoryImpl(dataSource);
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

            return new QuizChallengeDTO(
                    challenge.getId(),
                    challenger,
                    challenged,
                    quiz,
                    challenge.getMessage(),
                    challenge.getCreatedAt(),
                    challenge.getStatus()
            );
        } catch (SQLException e) {
            System.err.println("Error converting challenge to DTO: " + e.getMessage());
            return null;
        }
    }
}