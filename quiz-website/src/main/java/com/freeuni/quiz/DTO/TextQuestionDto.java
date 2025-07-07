package com.freeuni.quiz.DTO;

public class TextQuestionDto extends QuestionFormDto {
    private String correctAnswer;
    private String alternativeAnswers;

    public TextQuestionDto() {}

    public TextQuestionDto(String title, String categoryIdStr, String questionText, 
                          String correctAnswer, String alternativeAnswers) {
        super(title, "TEXT", categoryIdStr, questionText);
        this.correctAnswer = correctAnswer;
        this.alternativeAnswers = alternativeAnswers;
    }

    public String getCorrectAnswer() { 
        return correctAnswer; 
    }
    
    public void setCorrectAnswer(String correctAnswer) { 
        this.correctAnswer = correctAnswer; 
    }
    
    public String getAlternativeAnswers() { 
        return alternativeAnswers; 
    }
    
    public void setAlternativeAnswers(String alternativeAnswers) { 
        this.alternativeAnswers = alternativeAnswers; 
    }
} 