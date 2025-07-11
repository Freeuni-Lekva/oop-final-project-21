package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyCategory() {
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getCategoryName());
        assertNull(category.getDescription());
        assertFalse(category.isActive());
    }

    @Test
    void parameterizedConstructor_WithNameAndDescription_ShouldSetCorrectly() {
        String categoryName = "Science";
        String description = "Questions about science and nature";

        Category paramCategory = new Category(categoryName, description);

        assertEquals(categoryName, paramCategory.getCategoryName());
        assertEquals(description, paramCategory.getDescription());
        assertTrue(paramCategory.isActive());
        assertNull(paramCategory.getId());
    }

    @Test
    void fullParameterizedConstructor_ShouldSetAllFields() {
        Long id = 123L;
        String categoryName = "Math";
        String description = "Mathematics questions";
        boolean isActive = false;

        Category fullCategory = new Category(id, categoryName, description, isActive);

        assertEquals(id, fullCategory.getId());
        assertEquals(categoryName, fullCategory.getCategoryName());
        assertEquals(description, fullCategory.getDescription());
        assertEquals(isActive, fullCategory.isActive());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        Long expectedId = 456L;

        category.setId(expectedId);

        assertEquals(expectedId, category.getId());
    }

    @Test
    void setId_NullId_ShouldSetNull() {
        category.setId(null);

        assertNull(category.getId());
    }

    @Test
    void setCategoryName_ValidName_ShouldSetCorrectly() {
        String expectedName = "History";

        category.setCategoryName(expectedName);

        assertEquals(expectedName, category.getCategoryName());
    }

    @Test
    void setCategoryName_NullName_ShouldSetNull() {
        category.setCategoryName(null);

        assertNull(category.getCategoryName());
    }

    @Test
    void setCategoryName_EmptyName_ShouldSetEmpty() {
        String expectedName = "";

        category.setCategoryName(expectedName);

        assertEquals(expectedName, category.getCategoryName());
    }

    @Test
    void setDescription_ValidDescription_ShouldSetCorrectly() {
        String expectedDescription = "Questions about historical events and figures";

        category.setDescription(expectedDescription);

        assertEquals(expectedDescription, category.getDescription());
    }

    @Test
    void setDescription_NullDescription_ShouldSetNull() {
        category.setDescription(null);

        assertNull(category.getDescription());
    }

    @Test
    void setDescription_EmptyDescription_ShouldSetEmpty() {
        String expectedDescription = "";

        category.setDescription(expectedDescription);

        assertEquals(expectedDescription, category.getDescription());
    }

    @Test
    void setActive_True_ShouldSetTrue() {
        category.setActive(true);

        assertTrue(category.isActive());
    }

    @Test
    void setActive_False_ShouldSetFalse() {
        category.setActive(false);

        assertFalse(category.isActive());
    }

    @Test
    void setActive_ToggleStates_ShouldWorkCorrectly() {
        assertFalse(category.isActive());

        category.setActive(true);
        assertTrue(category.isActive());

        category.setActive(false);
        assertFalse(category.isActive());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        Long id = 789L;
        String categoryName = "Literature";
        String description = "Questions about books, authors, and literary works";
        boolean isActive = true;

        category.setId(id);
        category.setCategoryName(categoryName);
        category.setDescription(description);
        category.setActive(isActive);

        assertEquals(id, category.getId());
        assertEquals(categoryName, category.getCategoryName());
        assertEquals(description, category.getDescription());
        assertEquals(isActive, category.isActive());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        Category nullName = new Category(null, "description");
        Category nullDescription = new Category("name", null);
        Category bothNull = new Category(null, null);

        assertNull(nullName.getCategoryName());
        assertEquals("description", nullName.getDescription());
        assertTrue(nullName.isActive());

        assertEquals("name", nullDescription.getCategoryName());
        assertNull(nullDescription.getDescription());
        assertTrue(nullDescription.isActive());

        assertNull(bothNull.getCategoryName());
        assertNull(bothNull.getDescription());
        assertTrue(bothNull.isActive());
    }

    @Test
    void fullParameterizedConstructor_WithNullValues_ShouldHandleGracefully() {
        Category nullCategory = new Category(null, null, null, false);

        assertNull(nullCategory.getId());
        assertNull(nullCategory.getCategoryName());
        assertNull(nullCategory.getDescription());
        assertFalse(nullCategory.isActive());
    }

    @Test
    void setFields_LongStrings_ShouldHandleCorrectly() {
        String longName = "A".repeat(1000);
        String longDescription = "B".repeat(2000);

        category.setCategoryName(longName);
        category.setDescription(longDescription);

        assertEquals(longName, category.getCategoryName());
        assertEquals(longDescription, category.getDescription());
        assertEquals(1000, category.getCategoryName().length());
        assertEquals(2000, category.getDescription().length());
    }

    @Test
    void setFields_SpecialCharacters_ShouldHandleCorrectly() {
        String specialName = "Science & Technology!@#$%";
        String specialDescription = "Questions about science & technology with special chars: éñçåüö";

        category.setCategoryName(specialName);
        category.setDescription(specialDescription);

        assertEquals(specialName, category.getCategoryName());
        assertEquals(specialDescription, category.getDescription());
    }

    @Test
    void setFields_MultipleChanges_ShouldRetainLatestValues() {
        category.setCategoryName("Initial");
        category.setCategoryName("Updated");
        category.setCategoryName("Final");

        category.setDescription("First description");
        category.setDescription("Final description");

        assertEquals("Final", category.getCategoryName());
        assertEquals("Final description", category.getDescription());
    }
} 