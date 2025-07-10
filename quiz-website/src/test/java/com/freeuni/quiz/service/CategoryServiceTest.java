package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Category;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    private static DataSource dataSource;
    private CategoryService categoryService;
    private Category testCategory;

    @BeforeAll
    static void setupClass() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:categorytest;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=FALSE;DEFAULT_NULL_ORDERING=HIGH");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            
            stmt.execute("CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "hashPassword VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "firstName VARCHAR(100) NOT NULL," +
                    "lastName VARCHAR(100) NOT NULL," +
                    "userName VARCHAR(100) UNIQUE NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "imageURL VARCHAR(2083)," +
                    "bio TEXT" +
                    ")");
            
            stmt.execute("CREATE TABLE quiz_categories (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "category_name VARCHAR(64) NOT NULL," +
                    "description TEXT," +
                    "is_active BOOLEAN DEFAULT TRUE" +
                    ")");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        categoryService = new CategoryService(dataSource);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM quiz_categories");
        }

        testCategory = new Category();
        testCategory.setCategoryName("Mathematics");
        testCategory.setDescription("Math related questions");
    }

    @Test
    void createCategory_ValidCategory_ShouldReturnCategoryId() {
        Long result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertTrue(result > 0);
        assertTrue(testCategory.isActive());
    }

    @Test
    void createCategory_NullCategoryName_ShouldThrowException() {
        testCategory.setCategoryName(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name is required", exception.getMessage());
    }

    @Test
    void createCategory_EmptyCategoryName_ShouldThrowException() {
        testCategory.setCategoryName("   ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name is required", exception.getMessage());
    }

    @Test
    void createCategory_NullDescription_ShouldThrowException() {
        testCategory.setDescription(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category description is required", exception.getMessage());
    }

    @Test
    void createCategory_EmptyDescription_ShouldThrowException() {
        testCategory.setDescription("");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category description is required", exception.getMessage());
    }

    @Test
    void createCategory_ExistingCategoryName_ShouldThrowException() {
        categoryService.createCategory(testCategory);

        Category duplicateCategory = new Category();
        duplicateCategory.setCategoryName("Mathematics");
        duplicateCategory.setDescription("Another math category");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(duplicateCategory)
        );
        assertEquals("Category name already exists", exception.getMessage());
    }

    @Test
    void createCategory_CategoryNameTooLong_ShouldThrowException() {
        String longName = "a".repeat(101);
        testCategory.setCategoryName(longName);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name cannot exceed 100 characters", exception.getMessage());
    }

    @Test
    void createCategory_DescriptionTooLong_ShouldThrowException() {
        String longDescription = "a".repeat(501);
        testCategory.setDescription(longDescription);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category description cannot exceed 500 characters", exception.getMessage());
    }

    @Test
    void getCategoryById_ExistingCategory_ShouldReturnCategory() {
        Long categoryId = categoryService.createCategory(testCategory);

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertTrue(result.isPresent());
        assertEquals("Mathematics", result.get().getCategoryName());
    }

    @Test
    void getCategoryById_NonExistingCategory_ShouldReturnEmpty() {
        Optional<Category> result = categoryService.getCategoryById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllActiveCategories_ShouldReturnActiveCategories() {
        categoryService.createCategory(testCategory);
        
        Category category2 = new Category();
        category2.setCategoryName("Science");
        category2.setDescription("Science related questions");
        categoryService.createCategory(category2);

        List<Category> result = categoryService.getAllActiveCategories();

        assertEquals(2, result.size());
    }

    @Test
    void getAllActiveCategories_EmptyRepository_ShouldReturnEmptyList() {
        List<Category> result = categoryService.getAllActiveCategories();

        assertTrue(result.isEmpty());
    }

    @Test
    void updateCategory_ValidCategory_ShouldReturnTrue() {
        Long categoryId = categoryService.createCategory(testCategory);
        testCategory.setId(categoryId);
        testCategory.setDescription("Updated description");

        boolean result = categoryService.updateCategory(testCategory);

        assertTrue(result);
    }

    @Test
    void updateCategory_InvalidCategory_ShouldThrowException() {
        testCategory.setCategoryName(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));
    }

    @Test
    void updateCategory_NameAlreadyExists_ShouldThrowException() {
        categoryService.createCategory(testCategory);
        
        Category anotherCategory = new Category();
        anotherCategory.setCategoryName("Science");
        anotherCategory.setDescription("Science questions");
        Long anotherId = categoryService.createCategory(anotherCategory);
        
        anotherCategory.setId(anotherId);
        anotherCategory.setCategoryName("Mathematics");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.updateCategory(anotherCategory)
        );
        assertEquals("Category name already exists", exception.getMessage());
    }

    @Test
    void updateCategory_SameNameAsExisting_ShouldSucceed() {
        Long categoryId = categoryService.createCategory(testCategory);
        testCategory.setId(categoryId);
        testCategory.setDescription("Updated description but same name");

        boolean result = categoryService.updateCategory(testCategory);

        assertTrue(result);
    }

    @Test
    void deleteCategory_ExistingCategory_ShouldMarkAsInactive() {
        Long categoryId = categoryService.createCategory(testCategory);

        boolean result = categoryService.deleteCategory(categoryId);

        assertTrue(result);
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        assertTrue(category.isPresent());
        assertFalse(category.get().isActive());
    }

    @Test
    void deleteCategory_NonExistingCategory_ShouldReturnFalse() {
        boolean result = categoryService.deleteCategory(999L);

        assertFalse(result);
    }

    @Test
    void hardDeleteCategory_ShouldReturnRepositoryResult() {
        Long categoryId = categoryService.createCategory(testCategory);

        boolean result = categoryService.hardDeleteCategory(categoryId);

        assertTrue(result);
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        assertFalse(category.isPresent());
    }

    @Test
    void searchCategoriesByName_NoMatches_ShouldReturnEmptyList() {
        List<Category> result = categoryService.searchCategoriesByName("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void categoryExists_ExistingCategory_ShouldReturnTrue() {
        categoryService.createCategory(testCategory);

        boolean result = categoryService.categoryExists("Mathematics");

        assertTrue(result);
    }

    @Test
    void categoryExists_NonExistingCategory_ShouldReturnFalse() {
        boolean result = categoryService.categoryExists("NonExistent");

        assertFalse(result);
    }

    @Test
    void createCategory_SetsActiveToTrue() {
        testCategory.setActive(false);

        categoryService.createCategory(testCategory);

        assertTrue(testCategory.isActive());
    }

    @Test
    void updateCategory_ValidatesAllFields() {
        testCategory.setDescription("   ");
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));

        testCategory.setDescription("Valid Description");
        testCategory.setCategoryName("");
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));
    }
}