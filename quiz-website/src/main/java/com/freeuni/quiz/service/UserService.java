package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.bean.User;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.converter.UserConverter;
import com.freeuni.quiz.util.PasswordUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO;
    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.userDAO = new UserDAO(dataSource);
        this.dataSource = dataSource;
    }

    public boolean registerUser(String username, String password, String firstName,
                                String lastName, String email, String imageURL, String bio)
            throws SQLException, Exception {
        // Check if username is already taken
        if (userDAO.findByUsername(username) != null) {
            return false;
        }

        byte[] salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        String encodedSalt = PasswordUtil.encodeSalt(salt);

        User user = new User();
        user.setUserName(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setImageURL(imageURL != null && !imageURL.trim().isEmpty() ? imageURL : null);
        user.setBio(bio);
        user.setHashPassword(hashedPassword);
        user.setSalt(encodedSalt);

        return userDAO.addUser(user);
    }

    public UserDTO authenticateUser(String username, String password) throws Exception {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;

        byte[] saltBytes = PasswordUtil.decodeSalt(user.getSalt());
        String hashedInput = PasswordUtil.hashPassword(password, saltBytes);

        if (hashedInput.equals(user.getHashPassword())) {
            return UserConverter.toDTO(user);
        } else {
            return null;
        }
    }

    public boolean updateUserInfo(String username, String firstName, String lastName,
                                  String email, String imageURL, String bio) throws SQLException {

        String sql = "UPDATE users SET firstName = ?, lastName = ?, email = ?, imageURL = ?, bio = ? WHERE userName = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, imageURL);
            stmt.setString(5, bio);
            stmt.setString(6, username);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}
