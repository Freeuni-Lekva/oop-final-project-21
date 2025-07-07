package com.freeuni.quiz.DTO;

public class ImageQuestionDto extends QuestionFormDto {
    private String imageUrl;
    private String correctAnswer;

    public ImageQuestionDto() {}

    public ImageQuestionDto(String title, String categoryIdStr, String questionText, 
                           String imageUrl, String correctAnswer) {
        super(title, "IMAGE", categoryIdStr, questionText);
        this.imageUrl = imageUrl;
        this.correctAnswer = correctAnswer;
    }

    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
    
    public String getCorrectAnswer() { 
        return correctAnswer; 
    }
    
    public void setCorrectAnswer(String correctAnswer) { 
        this.correctAnswer = correctAnswer; 
    }
} 