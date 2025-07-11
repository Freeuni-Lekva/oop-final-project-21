package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.UserDAO;
import com.freeuni.quiz.bean.User;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.converter.UserConverter;
import com.freeuni.quiz.util.PasswordUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.*;

import static com.freeuni.quiz.converter.UserConverter.convertToDTO;

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
        user.setImageURL(validateImageURL(imageURL));
        user.setBio(bio);
        user.setHashPassword(hashedPassword);
        user.setSalt(encodedSalt);

        return userDAO.addUser(user);
    }

    private String validateImageURL(String imageURL) {
        String placeholder = "https://t3.ftcdn.net/jpg/05/16/27/58/360_F_516275801_f3Fsp17x6HQK0xQgDQEELoTuERO4SsWV.jpg";

        if (imageURL == null || imageURL.trim().isEmpty()) {
            return placeholder;
        }

        try {
            URI uri = URI.create(imageURL);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();

            if (responseCode == 200 && contentType != null && contentType.startsWith("image/")) {
                return imageURL;
            }
        } catch (IOException e) {
        }
        return placeholder;
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

    public boolean updateUserInfo(int id, String firstName, String lastName,
                                  String email, String imageURL, String bio) throws SQLException {

        User userEntity = userDAO.findById(id);

        userEntity.setBio(bio);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setImageURL(imageURL);

        return userDAO.updateUser(userEntity);
    }

    public List<UserDTO> searchUsers(String input) throws SQLException {
        List<User> users = userDAO.findUsers(input);
        List<UserDTO> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(convertToDTO(user));
        }

        return dtos;
    }

    public UserDTO findByUsername(String input) throws SQLException {
        User user = userDAO.findByUsername(input);
        if (user == null) return null;
        return UserConverter.toDTO(user);
    }

    public UserDTO findById(int id) throws SQLException {
        User user = userDAO.findById(id);
        if (user == null) return null;
        return UserConverter.toDTO(user);
    }

    public Map<Integer, UserDTO> findUsersByIds(Set<Integer> userIds) throws SQLException {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userDAO.findUsersByIds(userIds);
    }
}
