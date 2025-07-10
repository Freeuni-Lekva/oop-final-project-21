package com.freeuni.quiz.DAO;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserDAO {
    private final DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (hashPassword, salt, firstName, lastName, userName, email, imageURL, bio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getHashPassword());
            stmt.setString(2, user.getSalt());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getUserName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getImageURL());
            stmt.setString(8, user.getBio());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    public List<User> findUsers(String input) throws SQLException {
        String sql = "SELECT * FROM users WHERE userName LIKE ? OR firstName LIKE ? OR lastName LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            String likeQuery = "%" + input + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            stmt.setString(3, likeQuery);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }


    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE userName = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }


    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }


    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET hashPassword = ?, salt = ?, firstName = ?, lastName = ?, userName = ?, email = ?, imageURL = ?, bio = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getHashPassword());
            stmt.setString(2, user.getSalt()); // ✅ added
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getUserName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getImageURL());
            stmt.setString(8, user.getBio());
            stmt.setInt(9, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    public Map<Integer, UserDTO> findUsersByIds(Set<Integer> userIds) throws SQLException {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Build SQL with IN clause placeholders, e.g. "SELECT * FROM users WHERE id IN (?, ?, ...)"
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE id IN (");
        sql.append(userIds.stream().map(id -> "?").collect(Collectors.joining(",")));
        sql.append(")");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Integer id : userIds) {
                ps.setInt(index++, id);
            }

            ResultSet rs = ps.executeQuery();
            Map<Integer, UserDTO> result = new HashMap<>();
            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("imageUrl"),
                        rs.getString("bio")
                );
                result.put(user.getId(), user);
            }
            return result;
        }
    }


    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setHashPassword(rs.getString("hashPassword"));
        user.setSalt(rs.getString("salt"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setUserName(rs.getString("userName"));
        user.setEmail(rs.getString("email"));
        user.setImageURL(rs.getString("imageURL"));
        user.setBio(rs.getString("bio"));
        return user;
    }

}
