package com.freeuni.quiz.service;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.UserHistoryDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.repository.HistoryRepository;
import com.freeuni.quiz.repository.impl.HistoryRepositoryImpl;

import javax.sql.DataSource;
import java.util.*;

public class HistoryService {

    private final HistoryRepository historyRepository;
    private final QuizService quizService;

    public HistoryService(DataSource dataSource) {
        this.historyRepository = new HistoryRepositoryImpl(dataSource);
        this.quizService = new QuizService(dataSource);
    }

    public UserHistoryDTO getUserHistory(int userId, UserDTO user) {
        List<QuizCompletion> completions = historyRepository.getUserCompletions(userId);

        Map<QuizCompletion, Quiz> completionQuizMap = new LinkedHashMap<>();
        for (QuizCompletion completion : completions) {
            Optional<Quiz> quizOptional = quizService.getQuizById(completion.getTestId());
            if (quizOptional.isPresent()) {
                completionQuizMap.put(completion, quizOptional.get());
            }
        }

        int totalQuizzesTaken = historyRepository.getTotalQuizzesTaken(userId);
        double averageScore = historyRepository.getAverageScore(userId);
        int bestScore = historyRepository.getBestScore(userId);
        String mostPlayedCategory = historyRepository.getMostPlayedCategory(userId);
        int totalTimeTaken = historyRepository.getTotalTimeTaken(userId);

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
        return historyRepository.getUserRecentCompletions(userId, limit);
    }

    public int getTotalQuizzesTaken(int userId) {
        return historyRepository.getTotalQuizzesTaken(userId);
    }

    public double getAverageScore(int userId) {
        return historyRepository.getAverageScore(userId);
    }

    public Map<String, Integer> getCategoryDistribution(int userId) {
        return historyRepository.getCategoryDistribution(userId);
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
        List<QuizCompletion> completions = historyRepository.getUserCompletions(userId);
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