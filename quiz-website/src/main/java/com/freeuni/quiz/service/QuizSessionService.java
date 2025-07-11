package com.freeuni.quiz.service;

import com.freeuni.quiz.bean.QuizSession;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.ParticipantAnswer;
import com.freeuni.quiz.DAO.ParticipantAnswerDAO;
import com.freeuni.quiz.DAO.QuizDAO;
import com.freeuni.quiz.DAO.QuizSessionDAO;
import com.freeuni.quiz.DAO.impl.QuizSessionDAOImpl;
import com.freeuni.quiz.DAO.impl.QuizDAOImpl;
import com.freeuni.quiz.DAO.impl.ParticipantAnswerDAOImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class QuizSessionService {
    private final QuizSessionDAO quizSessionDAO;
    private final QuizDAO quizRepository;
    private final ParticipantAnswerDAO participantAnswerRepository;

    public QuizSessionService(DataSource dataSource) {
        this.quizSessionDAO = new QuizSessionDAOImpl(dataSource);
        this.quizRepository = new QuizDAOImpl(dataSource);
        this.participantAnswerRepository = new ParticipantAnswerDAOImpl(dataSource);
    }

    public boolean startQuizSession(Long participantId, Long quizId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            return false;
        }
        
        if (quizSessionDAO.hasActiveSession(participantId)) {
            return false;
        }
        
        Quiz quiz = quizOpt.get();
        
        QuizSession session = new QuizSession();
        session.setParticipantUserId(participantId);
        session.setTestId(quizId);
        session.setTimeAllocated(quiz.getTimeLimitMinutes());
        session.setCurrentQuestionNum(0L);
        session.setSessionStart(LocalDateTime.now());
        
        return quizSessionDAO.createSession(session);
    }

    public Optional<QuizSession> getActiveSession(Long participantId) {
        return quizSessionDAO.findByParticipant(participantId);
    }

    public boolean updateCurrentQuestion(Long participantId, Long questionNumber) {
        return quizSessionDAO.updateCurrentQuestion(participantId, questionNumber);
    }

    public boolean submitAnswer(Long participantId, Long testId, Long questionNumber, 
                               String answerText, Double pointsEarned, Integer timeSpent) {
        ParticipantAnswer answer = new ParticipantAnswer();
        answer.setParticipantUserId(participantId);
        answer.setTestId(testId);
        answer.setQuestionNumber(questionNumber);
        answer.setAnswerText(answerText);
        answer.setPointsEarned(pointsEarned);
        answer.setTimeSpentSeconds(timeSpent);
        
        boolean answerSaved = participantAnswerRepository.saveAnswer(answer);
        
        if (answerSaved) {
            return updateCurrentQuestion(participantId, questionNumber + 1);
        }
        
        return false;
    }

    public QuizResults getQuizResults(Long participantId, Long testId) {
        List<ParticipantAnswer> answers = participantAnswerRepository.getAllAnswers(participantId, testId);
        
        double totalScore = answers.stream()
            .mapToDouble(ParticipantAnswer::getPointsEarned)
            .sum();
        
        int totalQuestions = answers.size();
        
        return new QuizResults(participantId, testId, totalScore, totalQuestions, answers);
    }

    public List<Long> getAnsweredQuestions(Long participantId, Long testId) {
        return participantAnswerRepository.getAnsweredQuestionNumbers(participantId, testId);
    }

    public Optional<Double> getQuestionScore(Long participantId, Long testId, Long questionNumber) {
        return participantAnswerRepository.getAnswerScore(participantId, testId, questionNumber);
    }

    public boolean endQuizSession(Long participantId) {
        return quizSessionDAO.deleteSession(participantId);
    }

    public boolean hasActiveSession(Long participantId) {
        return quizSessionDAO.hasActiveSession(participantId);
    }

    public record QuizResults(Long participantId, Long testId, double totalScore, int totalQuestions,
                              List<ParticipantAnswer> answers) {
        public double getAverageScore() {
                return totalQuestions > 0 ? totalScore / totalQuestions : 0;
            }
        }
} 