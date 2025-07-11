package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.UserHistoryDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.DAO.HistoryDAO;
import com.freeuni.quiz.DAO.impl.HistoryDAOImpl;

import javax.sql.DataSource;
import java.util.*;

public class HistoryService {

    private final HistoryDAO historyDAO;
    private final QuizService quizService;

    public HistoryService(DataSource dataSource) {
        this.historyDAO = new HistoryDAOImpl(dataSource);
        this.quizService = new QuizService(dataSource);
    }

    public UserHistoryDTO getUserHistory(int userId, UserDTO user) {
        List<QuizCompletion> completions = historyDAO.getUserCompletions(userId);

        Map<QuizCompletion, Quiz> completionQuizMap = new LinkedHashMap<>();
        for (QuizCompletion completion : completions) {
            Optional<Quiz> quizOptional = quizService.getQuizById(completion.getTestId());
            if (quizOptional.isPresent()) {
                completionQuizMap.put(completion, quizOptional.get());
            }
        }

        int totalQuizzesTaken = historyDAO.getTotalQuizzesTaken(userId);
        double averageScore = historyDAO.getAverageScore(userId);
        int bestScore = historyDAO.getBestScore(userId);
        String mostPlayedCategory = historyDAO.getMostPlayedCategory(userId);
        int totalTimeTaken = historyDAO.getTotalTimeTaken(userId);

        return new UserHistoryDTO(
                user,
                completionQuizMap,
                totalQuizzesTaken,
                averageScore,
                bestScore,
                mostPlayedCategory,
                totalTimeTaken
        );
    }

    public List<QuizCompletion> getUserRecentCompletions(int userId, int limit) {
        return historyDAO.getUserRecentCompletions(userId, limit);
    }

    public int getTotalQuizzesTaken(int userId) {
        return historyDAO.getTotalQuizzesTaken(userId);
    }

    public double getAverageScore(int userId) {
        return historyDAO.getAverageScore(userId);
    }

    public Map<String, Integer> getCategoryDistribution(int userId) {
        return historyDAO.getCategoryDistribution(userId);
    }

    public Map<QuizCompletion, Quiz> getCompletionQuizMap(List<QuizCompletion> completions) {
        Map<QuizCompletion, Quiz> completionQuizMap = new LinkedHashMap<>();
        for (QuizCompletion completion : completions) {
            Optional<Quiz> quizOptional = quizService.getQuizById(completion.getTestId());
            if (quizOptional.isPresent()) {
                completionQuizMap.put(completion, quizOptional.get());
            }
        }
        return completionQuizMap;
    }

    public Map<Quiz, List<QuizCompletion>> getQuizCompletionsMap(int userId) {
        List<QuizCompletion> completions = historyDAO.getUserCompletions(userId);
        Map<Quiz, List<QuizCompletion>> quizCompletionsMap = new LinkedHashMap<>();

        for (QuizCompletion completion : completions) {
            Optional<Quiz> quizOptional = quizService.getQuizById(completion.getTestId());
            if (quizOptional.isPresent()) {
                Quiz quiz = quizOptional.get();
                if (!quizCompletionsMap.containsKey(quiz)) {
                    quizCompletionsMap.put(quiz, new ArrayList<>());
                }
                quizCompletionsMap.get(quiz).add(completion);
            }
        }

        return quizCompletionsMap;
    }
}