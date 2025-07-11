package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Category;
import com.freeuni.quiz.DAO.CategoryDAO;
import com.freeuni.quiz.DAO.impl.CategoryDAOImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(DataSource dataSource) {
        this.categoryDAO = new CategoryDAOImpl(dataSource);
    }

    public Long createCategory(Category category) {
        validateCategoryData(category);
        
        if (categoryDAO.existsByName(category.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        category.setActive(true);
        
        return categoryDAO.saveCategory(category);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryDAO.findById(categoryId);
    }

    public List<Category> getAllActiveCategories() {
        return categoryDAO.findAllActive();
    }

    public List<Category> searchCategoriesByName(String searchTerm) {
        return categoryDAO.searchByName(searchTerm);
    }

    public boolean updateCategory(Category category) {
        validateCategoryData(category);
        
        Optional<Category> existingCategoryOpt = categoryDAO.findById(category.getId());
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            if (!existingCategory.getCategoryName().equals(category.getCategoryName()) &&
                categoryDAO.existsByName(category.getCategoryName())) {
                throw new IllegalArgumentException("Category name already exists");
            }
        }
        
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(Long categoryId) {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setActive(false);
            return categoryDAO.updateCategory(category);
        }
        return false;
    }

    public boolean hardDeleteCategory(Long categoryId) {
        return categoryDAO.deleteCategory(categoryId);
    }

    public boolean categoryExists(String categoryName) {
        return categoryDAO.existsByName(categoryName);
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