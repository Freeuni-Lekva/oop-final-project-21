package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Category;
import com.freeuni.quiz.repository.CategoryRepository;
import com.freeuni.quiz.repository.impl.CategoryRepositoryImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(DataSource dataSource) {
        this.categoryRepository = new CategoryRepositoryImpl(dataSource);
    }

    public Long createCategory(Category category) {
        validateCategoryData(category);
        
        if (categoryRepository.existsByName(category.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        category.setActive(true);
        
        return categoryRepository.saveCategory(category);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllActive();
    }

    public List<Category> searchCategoriesByName(String searchTerm) {
        return categoryRepository.searchByName(searchTerm);
    }

    public boolean updateCategory(Category category) {
        validateCategoryData(category);
        
        Optional<Category> existingCategoryOpt = categoryRepository.findById(category.getId());
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            if (!existingCategory.getCategoryName().equals(category.getCategoryName()) &&
                categoryRepository.existsByName(category.getCategoryName())) {
                throw new IllegalArgumentException("Category name already exists");
            }
        }
        
        return categoryRepository.updateCategory(category);
    }

    public boolean deleteCategory(Long categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setActive(false);
            return categoryRepository.updateCategory(category);
        }
        return false;
    }

    public boolean hardDeleteCategory(Long categoryId) {
        return categoryRepository.deleteCategory(categoryId);
    }

    public boolean categoryExists(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }

    private void validateCategoryData(Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        
        if (category.getDescription() == null || category.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Category description is required");
        }
        
        if (category.getCategoryName().length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
        
        if (category.getDescription().length() > 500) {
            throw new IllegalArgumentException("Category description cannot exceed 500 characters");
        }
    }
} 