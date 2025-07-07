package com.freeuni.quiz.repository;

import com.freeuni.quiz.bean.Question;
import com.freeuni.quiz.bean.QuestionType;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    Long saveQuestion(Question question);

    Optional<Question> findById(Long questionId);

    List<Question> findByAuthor(Long authorUserId, int offset, int limit);

    List<Question> findByCategory(Long categoryId, int offset, int limit);

    List<Question> findByType(QuestionType questionType, int offset, int limit);

    List<Question> searchByTitle(String searchTerm, int offset, int limit);

    boolean updateQuestion(Question question);

    boolean deleteQuestion(Long questionId);
}
