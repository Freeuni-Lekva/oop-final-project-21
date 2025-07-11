package com.freeuni.quiz.DTO;

import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;

import java.util.Map;

public class UserHistoryDTO {
    private UserDTO user;
    private Map<QuizCompletion, Quiz> completions;
    private int totalQuizzesTaken;
    private double averageScore;
    private int bestScore;
    private String mostPlayedCategory;
    private int totalTimeTaken; // in minutes

    public UserHistoryDTO() {}

    public UserHistoryDTO(UserDTO user, Map<QuizCompletion, Quiz> completions,
                          int totalQuizzesTaken, double averageScore,
                          int bestScore, String mostPlayedCategory, int totalTimeTaken) {
        this.user = user;
        this.completions = completions;
        this.totalQuizzesTaken = totalQuizzesTaken;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.mostPlayedCategory = mostPlayedCategory;
        this.totalTimeTaken = totalTimeTaken;
    }

    // Getters and setters
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public Map<QuizCompletion, Quiz> getCompletions() { return completions; }
    public void setCompletions(Map<QuizCompletion, Quiz> completions) { this.completions = completions; }

    public int getTotalQuizzesTaken() { return totalQuizzesTaken; }
    public void setTotalQuizzesTaken(int totalQuizzesTaken) { this.totalQuizzesTaken = totalQuizzesTaken; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public int getBestScore() { return bestScore; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }

    public String getMostPlayedCategory() { return mostPlayedCategory; }
    public void setMostPlayedCategory(String mostPlayedCategory) { this.mostPlayedCategory = mostPlayedCategory; }

    public int getTotalTimeTaken() { return totalTimeTaken; }
    public void setTotalTimeTaken(int totalTimeTaken) { this.totalTimeTaken = totalTimeTaken; }
}