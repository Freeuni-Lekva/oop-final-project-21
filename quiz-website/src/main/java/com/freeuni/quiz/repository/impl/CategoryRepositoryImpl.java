package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.Category;
import com.freeuni.quiz.repository.CategoryRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl implements CategoryRepository {
    private final DataSource dataSource;

    public CategoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long saveCategory(Category category) {
        String sql = "INSERT INTO quiz_categories (category_name, description, is_active) VALUES (?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                throw new SQLException("Failed to insert category, no ID obtained");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving category", e);
        }
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        String sql = "SELECT * FROM quiz_categories WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, categoryId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCategory(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by ID", e);
        }
    }

    @Override
    public List<Category> findAllActive() {
        String sql = "SELECT * FROM quiz_categories WHERE is_active = true ORDER BY category_name";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            return executeCategoryQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all active categories", e);
        }
    }

    @Override
    public List<Category> searchByName(String searchTerm) {
        String sql = "SELECT * FROM quiz_categories WHERE category_name LIKE ? ORDER BY category_name";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + searchTerm + "%");
            
            return executeCategoryQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching categories by name", e);
        }
    }

    @Override
    public boolean updateCategory(Category category) {
        String sql = "UPDATE quiz_categories SET category_name = ?, description = ?, is_active = ? WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            statement.setLong(4, category.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public boolean existsByName(String categoryName) {
        String sql = "SELECT COUNT(*) FROM quiz_categories WHERE category_name = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, categoryName);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking category existence", e);
        }
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        String sql = "DELETE FROM quiz_categories WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, categoryId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private List<Category> executeCategoryQuery(PreparedStatement statement) throws SQLException {
        List<Category> categories = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(mapResultSetToCategory(resultSet));
            }
        }
        
        return categories;
    }

    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getLong("id"));
        category.setCategoryName(resultSet.getString("category_name"));
        category.setDescription(resultSet.getString("description"));
        category.setActive(resultSet.getBoolean("is_active"));
        
        return category;
    }
} 