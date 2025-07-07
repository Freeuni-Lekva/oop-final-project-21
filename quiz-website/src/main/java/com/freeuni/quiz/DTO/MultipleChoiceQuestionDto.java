package com.freeuni.quiz.DTO;

public class MultipleChoiceQuestionDto extends QuestionFormDto {
    private String[] options;
    private String correctOptionStr;

    public MultipleChoiceQuestionDto() {}

    public MultipleChoiceQuestionDto(String title, String categoryIdStr, String questionText, 
                                   String[] options, String correctOptionStr) {
        super(title, "MULTIPLE_CHOICE", categoryIdStr, questionText);
        this.options = options;
        this.correctOptionStr = correctOptionStr;
    }

    public String[] getOptions() { 
        return options; 
    }
    
    public void setOptions(String[] options) { 
        this.options = options; 
    }
    
    public String getCorrectOptionStr() { 
        return correctOptionStr; 
    }
    
    public void setCorrectOptionStr(String correctOptionStr) { 
        this.correctOptionStr = correctOptionStr; 
    }
} 