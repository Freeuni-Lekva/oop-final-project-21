package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.DTO.PopularQuizDTO;

import java.util.List;
import java.util.Optional;

public interface QuizDAO {
    Long saveQuiz(Quiz quiz);

    Optional<Quiz> findById(Long quizId);

    List<Quiz> findByCreator(Long creatorUserId, int offset, int limit);

    List<Quiz> findByCategory(Long categoryId, int offset, int limit);

    List<Quiz> findAll(int offset, int limit);

    boolean updateQuiz(Quiz quiz);

    void updateLastQuestionNumber(Long quizId, Long questionNumber);

    boolean deleteQuiz(Long quizId);

    List<PopularQuizDTO> findPopularQuizzesWithCompletionCount(int limit);

    List<Quiz> findRecentlyCreatedQuizzes(int limit);

    List<Quiz> findRecentlyCreatedByUser(Long userId, int limit);
}
