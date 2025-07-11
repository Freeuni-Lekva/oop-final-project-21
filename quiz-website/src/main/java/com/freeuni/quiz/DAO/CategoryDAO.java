package com.freeuni.quiz.DAO;

import com.freeuni.quiz.bean.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {

    Long saveCategory(Category category);

    Optional<Category> findById(Long categoryId);

    List<Category> findAllActive();

    List<Category> searchByName(String searchTerm);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long categoryId);

    boolean existsByName(String categoryName);
}
