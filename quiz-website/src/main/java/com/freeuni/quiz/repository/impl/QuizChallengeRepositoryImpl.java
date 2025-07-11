package com.freeuni.quiz.repository.impl;

import com.freeuni.quiz.bean.QuizChallenge;
import com.freeuni.quiz.repository.QuizChallengeRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizChallengeRepositoryImpl implements QuizChallengeRepository {
    private final DataSource dataSource;

    public QuizChallengeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createChallenge(QuizChallenge challenge) {
        String sql = "INSERT INTO quiz_challenges (challenger_user_id, challenged_user_id, quiz_id, " +
                "message, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, challenge.getChallengerUserId());
            statement.setInt(2, challenge.getChallengedUserId());
            statement.setLong(3, challenge.getQuizId());
            statement.setString(4, challenge.getMessage());
            statement.setString(5, challenge.getStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        challenge.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating quiz challenge", e);
        }
    }

    @Override
    public List<QuizChallenge> getChallengesReceivedByUser(int userId) {
        String sql = "SELECT * FROM quiz_challenges WHERE challenged_user_id = ? ORDER BY created_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            return executeChallengeQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting challenges received by user", e);
        }
    }

    @Override
    public List<QuizChallenge> getChallengesSentByUser(int userId) {
        String sql = "SELECT * FROM quiz_challenges WHERE challenger_user_id = ? ORDER BY created_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            return executeChallengeQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting challenges sent by user", e);
        }
    }

    @Override
    public Optional<QuizChallenge> getChallengeById(Long challengeId) {
        String sql = "SELECT * FROM quiz_challenges WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, challengeId);
            List<QuizChallenge> challenges = executeChallengeQuery(statement);

            return challenges.isEmpty() ? Optional.empty() : Optional.of(challenges.get(0));
        } catch (SQLException e) {
            throw new RuntimeException("Error getting challenge by ID", e);
        }
    }

    @Override
    public boolean updateChallengeStatus(Long challengeId, String status) {
        String sql = "UPDATE quiz_challenges SET status = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, challengeId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating challenge status", e);
        }
    }

    @Override
    public boolean challengeExists(int challengerId, int challengedId, Long quizId) {
        String sql = "SELECT COUNT(*) FROM quiz_challenges WHERE challenger_user_id = ? AND challenged_user_id = ? AND quiz_id = ? AND status = 'PENDING'";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, challengerId);
            statement.setInt(2, challengedId);
            statement.setLong(3, quizId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if challenge exists", e);
        }
    }

    @Override
    public boolean deleteChallenge(Long challengeId) {
        String sql = "DELETE FROM quiz_challenges WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, challengeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting challenge", e);
        }
    }

    private List<QuizChallenge> executeChallengeQuery(PreparedStatement statement) throws SQLException {
        List<QuizChallenge> challenges = new ArrayList<>();

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                challenges.add(mapResultSetToChallenge(resultSet));
            }
        }

        return challenges;
    }

    private QuizChallenge mapResultSetToChallenge(ResultSet resultSet) throws SQLException {
        QuizChallenge challenge = new QuizChallenge();
        challenge.setId(resultSet.getLong("id"));
        challenge.setChallengerUserId(resultSet.getInt("challenger_user_id"));
        challenge.setChallengedUserId(resultSet.getInt("challenged_user_id"));
        challenge.setQuizId(resultSet.getLong("quiz_id"));
        challenge.setMessage(resultSet.getString("message"));
        challenge.setCreatedAt(resultSet.getTimestamp("created_at"));
        challenge.setStatus(resultSet.getString("status"));

        return challenge;
    }
}