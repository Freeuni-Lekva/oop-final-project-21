package com.freeuni.quiz.DTO;

public class QuizFormDto {
    private String title;
    private String description;
    private String categoryIdStr;
    private String timeLimitStr;
    private String validationError;

    public QuizFormDto() {}

    public QuizFormDto(String title, String description, String categoryIdStr, String timeLimitStr) {
        this.title = title;
        this.description = description;
        this.categoryIdStr = categoryIdStr;
        this.timeLimitStr = timeLimitStr;
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getCategoryIdStr() { 
        return categoryIdStr; 
    }
    
    public void setCategoryIdStr(String categoryIdStr) { 
        this.categoryIdStr = categoryIdStr; 
    }
    
    public String getTimeLimitStr() { 
        return timeLimitStr; 
    }
    
    public void setTimeLimitStr(String timeLimitStr) { 
        this.timeLimitStr = timeLimitStr; 
    }
    
    public String getValidationError() { 
        return validationError; 
    }
    
    public void setValidationError(String validationError) { 
        this.validationError = validationError; 
    }
} 