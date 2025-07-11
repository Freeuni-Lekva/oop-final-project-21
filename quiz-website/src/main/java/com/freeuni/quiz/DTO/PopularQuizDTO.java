package com.freeuni.quiz.DTO;

import com.freeuni.quiz.bean.Quiz;

public class PopularQuizDTO {
    private Quiz quiz;
    private final int completionCount;

    public PopularQuizDTO(Quiz quiz, int completionCount) {
        this.quiz = quiz;
        this.completionCount = completionCount;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getCompletionCount() {
        return completionCount;
    }

}