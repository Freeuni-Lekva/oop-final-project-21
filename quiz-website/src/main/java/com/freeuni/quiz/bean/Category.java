package com.freeuni.quiz.bean;

public class Category {
    private Long id;
    private String categoryName;
    private String description;
    private boolean isActive;

    public Category() {}

    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = true;
    }

    public Category(Long id, String categoryName, String description, boolean isActive) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
}
