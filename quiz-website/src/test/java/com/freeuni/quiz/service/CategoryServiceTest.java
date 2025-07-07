package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.Category;
import com.freeuni.quiz.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
        
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Mathematics");
        testCategory.setDescription("Math related questions");
    }

    @Test
    void createCategory_ValidCategory_ShouldReturnCategoryId() {
        Long expectedId = 1L;
        when(categoryRepository.existsByName("Mathematics")).thenReturn(false);
        when(categoryRepository.saveCategory(any(Category.class))).thenReturn(expectedId);

        Long result = categoryService.createCategory(testCategory);

        assertEquals(expectedId, result);
        assertTrue(testCategory.isActive());
        verify(categoryRepository).existsByName("Mathematics");
        verify(categoryRepository).saveCategory(testCategory);
    }

    @Test
    void createCategory_NullCategoryName_ShouldThrowException() {
        testCategory.setCategoryName(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name is required", exception.getMessage());
        verify(categoryRepository, never()).saveCategory(any());
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
        when(categoryRepository.existsByName("Mathematics")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name already exists", exception.getMessage());
        verify(categoryRepository, never()).saveCategory(any());
    }

    @Test
    void createCategory_CategoryNameTooLong_ShouldThrowException() {
        String longName = "a".repeat(101); // 101 characters
        testCategory.setCategoryName(longName);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category name cannot exceed 100 characters", exception.getMessage());
    }

    @Test
    void createCategory_DescriptionTooLong_ShouldThrowException() {
        String longDescription = "a".repeat(501); // 501 characters
        testCategory.setDescription(longDescription);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        assertEquals("Category description cannot exceed 500 characters", exception.getMessage());
    }

    @Test
    void getCategoryById_ExistingCategory_ShouldReturnCategory() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertTrue(result.isPresent());
        assertEquals(testCategory, result.get());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_NonExistingCategory_ShouldReturnEmpty() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertFalse(result.isPresent());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getAllActiveCategories_ShouldReturnActiveCategories() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setCategoryName("Science");
        category2.setActive(true);
        
        List<Category> expectedCategories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findAllActive()).thenReturn(expectedCategories);

        List<Category> result = categoryService.getAllActiveCategories();

        assertEquals(expectedCategories, result);
        verify(categoryRepository).findAllActive();
    }

    @Test
    void getAllActiveCategories_EmptyRepository_ShouldReturnEmptyList() {
        when(categoryRepository.findAllActive()).thenReturn(new ArrayList<>());

        List<Category> result = categoryService.getAllActiveCategories();

        assertTrue(result.isEmpty());
        verify(categoryRepository).findAllActive();
    }

    @Test
    void updateCategory_ValidCategory_ShouldReturnTrue() {
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(categoryRepository.updateCategory(testCategory)).thenReturn(true);

        boolean result = categoryService.updateCategory(testCategory);

        assertTrue(result);
        verify(categoryRepository).updateCategory(testCategory);
    }

    @Test
    void updateCategory_InvalidCategory_ShouldThrowException() {
        testCategory.setCategoryName(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));
        verify(categoryRepository, never()).updateCategory(any());
    }

    @Test
    void updateCategory_NameAlreadyExists_ShouldThrowException() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setCategoryName("OldName");
        
        testCategory.setCategoryName("NewName");
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("NewName")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.updateCategory(testCategory)
        );
        assertEquals("Category name already exists", exception.getMessage());
        verify(categoryRepository, never()).updateCategory(any());
    }

    @Test
    void updateCategory_SameNameAsExisting_ShouldSucceed() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setCategoryName("Mathematics");
        
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.updateCategory(testCategory)).thenReturn(true);

        boolean result = categoryService.updateCategory(testCategory);

        assertTrue(result);
        verify(categoryRepository).updateCategory(testCategory);
        verify(categoryRepository, never()).existsByName(any());
    }

    @Test
    void deleteCategory_ExistingCategory_ShouldMarkAsInactive() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.updateCategory(testCategory)).thenReturn(true);

        boolean result = categoryService.deleteCategory(categoryId);

        assertTrue(result);
        assertFalse(testCategory.isActive());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).updateCategory(testCategory);
    }

    @Test
    void deleteCategory_NonExistingCategory_ShouldReturnFalse() {
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        boolean result = categoryService.deleteCategory(categoryId);

        assertFalse(result);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).updateCategory(any());
    }

    @Test
    void hardDeleteCategory_ShouldReturnRepositoryResult() {
        Long categoryId = 1L;
        when(categoryRepository.deleteCategory(categoryId)).thenReturn(true);

        boolean result = categoryService.hardDeleteCategory(categoryId);

        assertTrue(result);
        verify(categoryRepository).deleteCategory(categoryId);
    }

    @Test
    void searchCategoriesByName_ValidSearchTerm_ShouldReturnMatchingCategories() {
        String searchTerm = "math";
        List<Category> expectedCategories = Collections.singletonList(testCategory);
        when(categoryRepository.searchByName(searchTerm)).thenReturn(expectedCategories);

        List<Category> result = categoryService.searchCategoriesByName(searchTerm);

        assertEquals(expectedCategories, result);
        verify(categoryRepository).searchByName(searchTerm);
    }

    @Test
    void searchCategoriesByName_NoMatches_ShouldReturnEmptyList() {
        String searchTerm = "nonexistent";
        when(categoryRepository.searchByName(searchTerm)).thenReturn(new ArrayList<>());

        List<Category> result = categoryService.searchCategoriesByName(searchTerm);

        assertTrue(result.isEmpty());
        verify(categoryRepository).searchByName(searchTerm);
    }

    @Test
    void categoryExists_ExistingCategory_ShouldReturnTrue() {
        String categoryName = "Mathematics";
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);

        boolean result = categoryService.categoryExists(categoryName);

        assertTrue(result);
        verify(categoryRepository).existsByName(categoryName);
    }

    @Test
    void categoryExists_NonExistingCategory_ShouldReturnFalse() {
        String categoryName = "NonExistent";
        when(categoryRepository.existsByName(categoryName)).thenReturn(false);

        boolean result = categoryService.categoryExists(categoryName);

        assertFalse(result);
        verify(categoryRepository).existsByName(categoryName);
    }

    @Test
    void createCategory_SetsActiveToTrue() {
        testCategory.setActive(false);
        when(categoryRepository.existsByName("Mathematics")).thenReturn(false);
        when(categoryRepository.saveCategory(any(Category.class))).thenReturn(1L);

        categoryService.createCategory(testCategory);

        assertTrue(testCategory.isActive());
        verify(categoryRepository).saveCategory(testCategory);
    }

    @Test
    void updateCategory_ValidatesAllFields() {
        testCategory.setDescription("   ");
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));
        
        testCategory.setDescription("Valid Description");
        testCategory.setCategoryName("");
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(testCategory));
    }

    @Test
    void createCategory_ExactLimits_ShouldSucceed() {
        String maxName = "a".repeat(100);
        String maxDescription = "a".repeat(500);
        
        testCategory.setCategoryName(maxName);
        testCategory.setDescription(maxDescription);
        
        when(categoryRepository.existsByName(maxName)).thenReturn(false);
        when(categoryRepository.saveCategory(any(Category.class))).thenReturn(1L);

        assertDoesNotThrow(() -> categoryService.createCategory(testCategory));
        verify(categoryRepository).saveCategory(testCategory);
    }
} 