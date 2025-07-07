package com.freeuni.quiz.bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyUser() {
        // Act & Assert
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertNull(user.getHashPassword());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getUserName());
        assertNull(user.getEmail());
        assertNull(user.getImageURL());
        assertNull(user.getBio());
        assertNull(user.getSalt());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // Arrange
        int id = 123;
        String hashPassword = "hashedpass123";
        String firstName = "John";
        String lastName = "Doe";
        String userName = "johndoe";
        String email = "john.doe@example.com";
        String imageURL = "https://example.com/profile.jpg";
        String bio = "Software developer";
        String salt = "randomsalt123";

        // Act
        User paramUser = new User(id, hashPassword, firstName, lastName, userName, email, imageURL, bio, salt);

        // Assert
        assertEquals(id, paramUser.getId());
        assertEquals(hashPassword, paramUser.getHashPassword());
        assertEquals(firstName, paramUser.getFirstName());
        assertEquals(lastName, paramUser.getLastName());
        assertEquals(userName, paramUser.getUserName());
        assertEquals(email, paramUser.getEmail());
        assertEquals(imageURL, paramUser.getImageURL());
        assertEquals(bio, paramUser.getBio());
        assertEquals(salt, paramUser.getSalt());
    }

    @Test
    void setId_ValidId_ShouldSetCorrectly() {
        // Arrange
        int expectedId = 456;

        // Act
        user.setId(expectedId);

        // Assert
        assertEquals(expectedId, user.getId());
    }

    @Test
    void setId_ZeroId_ShouldSetCorrectly() {
        // Arrange
        int expectedId = 0;

        // Act
        user.setId(expectedId);

        // Assert
        assertEquals(expectedId, user.getId());
    }

    @Test
    void setId_NegativeId_ShouldSetCorrectly() {
        // Arrange
        int expectedId = -1;

        // Act
        user.setId(expectedId);

        // Assert
        assertEquals(expectedId, user.getId());
    }

    @Test
    void setHashPassword_ValidPassword_ShouldSetCorrectly() {
        // Arrange
        String expectedPassword = "hashed_password_123";

        // Act
        user.setHashPassword(expectedPassword);

        // Assert
        assertEquals(expectedPassword, user.getHashPassword());
    }

    @Test
    void setHashPassword_NullPassword_ShouldSetNull() {
        // Act
        user.setHashPassword(null);

        // Assert
        assertNull(user.getHashPassword());
    }

    @Test
    void setHashPassword_EmptyPassword_ShouldSetEmpty() {
        // Arrange
        String expectedPassword = "";

        // Act
        user.setHashPassword(expectedPassword);

        // Assert
        assertEquals(expectedPassword, user.getHashPassword());
    }

    @Test
    void setFirstName_ValidName_ShouldSetCorrectly() {
        // Arrange
        String expectedName = "Jane";

        // Act
        user.setFirstName(expectedName);

        // Assert
        assertEquals(expectedName, user.getFirstName());
    }

    @Test
    void setFirstName_NullName_ShouldSetNull() {
        // Act
        user.setFirstName(null);

        // Assert
        assertNull(user.getFirstName());
    }

    @Test
    void setFirstName_EmptyName_ShouldSetEmpty() {
        // Arrange
        String expectedName = "";

        // Act
        user.setFirstName(expectedName);

        // Assert
        assertEquals(expectedName, user.getFirstName());
    }

    @Test
    void setLastName_ValidName_ShouldSetCorrectly() {
        // Arrange
        String expectedName = "Smith";

        // Act
        user.setLastName(expectedName);

        // Assert
        assertEquals(expectedName, user.getLastName());
    }

    @Test
    void setLastName_NullName_ShouldSetNull() {
        // Act
        user.setLastName(null);

        // Assert
        assertNull(user.getLastName());
    }

    @Test
    void setUserName_ValidUserName_ShouldSetCorrectly() {
        // Arrange
        String expectedUserName = "janesmith123";

        // Act
        user.setUserName(expectedUserName);

        // Assert
        assertEquals(expectedUserName, user.getUserName());
    }

    @Test
    void setUserName_NullUserName_ShouldSetNull() {
        // Act
        user.setUserName(null);

        // Assert
        assertNull(user.getUserName());
    }

    @Test
    void setEmail_ValidEmail_ShouldSetCorrectly() {
        // Arrange
        String expectedEmail = "user@example.com";

        // Act
        user.setEmail(expectedEmail);

        // Assert
        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    void setEmail_NullEmail_ShouldSetNull() {
        // Act
        user.setEmail(null);

        // Assert
        assertNull(user.getEmail());
    }

    @Test
    void setImageURL_ValidURL_ShouldSetCorrectly() {
        // Arrange
        String expectedURL = "https://example.com/avatar.png";

        // Act
        user.setImageURL(expectedURL);

        // Assert
        assertEquals(expectedURL, user.getImageURL());
    }

    @Test
    void setImageURL_NullURL_ShouldSetNull() {
        // Act
        user.setImageURL(null);

        // Assert
        assertNull(user.getImageURL());
    }

    @Test
    void setBio_ValidBio_ShouldSetCorrectly() {
        // Arrange
        String expectedBio = "I am a software engineer with 5 years of experience.";

        // Act
        user.setBio(expectedBio);

        // Assert
        assertEquals(expectedBio, user.getBio());
    }

    @Test
    void setBio_NullBio_ShouldSetNull() {
        // Act
        user.setBio(null);

        // Assert
        assertNull(user.getBio());
    }

    @Test
    void setBio_EmptyBio_ShouldSetEmpty() {
        // Arrange
        String expectedBio = "";

        // Act
        user.setBio(expectedBio);

        // Assert
        assertEquals(expectedBio, user.getBio());
    }

    @Test
    void setSalt_ValidSalt_ShouldSetCorrectly() {
        // Arrange
        String expectedSalt = "random_salt_value_123";

        // Act
        user.setSalt(expectedSalt);

        // Assert
        assertEquals(expectedSalt, user.getSalt());
    }

    @Test
    void setSalt_NullSalt_ShouldSetNull() {
        // Act
        user.setSalt(null);

        // Assert
        assertNull(user.getSalt());
    }

    @Test
    void allFields_SetAndGet_ShouldWorkCorrectly() {
        // Arrange
        int id = 999;
        String hashPassword = "complex_hash_password";
        String firstName = "Alice";
        String lastName = "Johnson";
        String userName = "alicej";
        String email = "alice.johnson@company.com";
        String imageURL = "https://cdn.example.com/users/alice.jpg";
        String bio = "Data scientist and machine learning enthusiast.";
        String salt = "unique_salt_for_alice";

        // Act
        user.setId(id);
        user.setHashPassword(hashPassword);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setEmail(email);
        user.setImageURL(imageURL);
        user.setBio(bio);
        user.setSalt(salt);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(hashPassword, user.getHashPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(userName, user.getUserName());
        assertEquals(email, user.getEmail());
        assertEquals(imageURL, user.getImageURL());
        assertEquals(bio, user.getBio());
        assertEquals(salt, user.getSalt());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldSetNullFields() {
        // Act
        User nullUser = new User(0, null, null, null, null, null, null, null, null);

        // Assert
        assertEquals(0, nullUser.getId());
        assertNull(nullUser.getHashPassword());
        assertNull(nullUser.getFirstName());
        assertNull(nullUser.getLastName());
        assertNull(nullUser.getUserName());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getImageURL());
        assertNull(nullUser.getBio());
        assertNull(nullUser.getSalt());
    }

    @Test
    void parameterizedConstructor_WithEmptyStrings_ShouldSetEmptyFields() {
        // Act
        User emptyUser = new User(1, "", "", "", "", "", "", "", "");

        // Assert
        assertEquals(1, emptyUser.getId());
        assertEquals("", emptyUser.getHashPassword());
        assertEquals("", emptyUser.getFirstName());
        assertEquals("", emptyUser.getLastName());
        assertEquals("", emptyUser.getUserName());
        assertEquals("", emptyUser.getEmail());
        assertEquals("", emptyUser.getImageURL());
        assertEquals("", emptyUser.getBio());
        assertEquals("", emptyUser.getSalt());
    }

    @Test
    void setFields_MultipleChanges_ShouldRetainLatestValues() {
        // Arrange & Act
        user.setFirstName("Initial");
        user.setFirstName("Updated");
        user.setFirstName("Final");

        user.setEmail("first@example.com");
        user.setEmail("final@example.com");

        // Assert
        assertEquals("Final", user.getFirstName());
        assertEquals("final@example.com", user.getEmail());
    }

    @Test
    void longStringFields_ShouldHandleCorrectly() {
        // Arrange
        String longBio = "A".repeat(1000); // Very long bio
        String longImageURL = "https://example.com/" + "path/".repeat(100) + "image.jpg";

        // Act
        user.setBio(longBio);
        user.setImageURL(longImageURL);

        // Assert
        assertEquals(longBio, user.getBio());
        assertEquals(longImageURL, user.getImageURL());
        assertEquals(1000, user.getBio().length());
    }

    @Test
    void specialCharacters_ShouldHandleCorrectly() {
        // Arrange
        String specialName = "José";
        String specialEmail = "user+test@example.com";
        String specialBio = "Bio with émojis 🚀 and spëcial chars!";

        // Act
        user.setFirstName(specialName);
        user.setEmail(specialEmail);
        user.setBio(specialBio);

        // Assert
        assertEquals(specialName, user.getFirstName());
        assertEquals(specialEmail, user.getEmail());
        assertEquals(specialBio, user.getBio());
    }
} 