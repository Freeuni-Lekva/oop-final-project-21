package com.freeuni.quiz.DTO;

public class QuestionFormDto {
    private String title;
    private String questionType;
    private String categoryIdStr;
    private String questionText;
    private String validationError;

    public QuestionFormDto() {}

    public QuestionFormDto(String title, String questionType, String categoryIdStr, String questionText) {
        this.title = title;
        this.questionType = questionType;
        this.categoryIdStr = categoryIdStr;
        this.questionText = questionText;
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getQuestionType() { 
        return questionType; 
    }
    
    public void setQuestionType(String questionType) { 
        this.questionType = questionType; 
    }
    
    public String getCategoryIdStr() { 
        return categoryIdStr; 
    }
    
    public void setCategoryIdStr(String categoryIdStr) { 
        this.categoryIdStr = categoryIdStr; 
    }
    
    public String getQuestionText() { 
        return questionText; 
    }
    
    public void setQuestionText(String questionText) { 
        this.questionText = questionText; 
    }
    
    public String getValidationError() { 
        return validationError; 
    }
    
    public void setValidationError(String validationError) { 
        this.validationError = validationError; 
    }
} 