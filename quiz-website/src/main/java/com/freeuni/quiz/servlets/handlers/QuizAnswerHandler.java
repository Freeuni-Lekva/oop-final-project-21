package com.freeuni.quiz.servlets.handlers;

import com.freeuni.quiz.bean.*;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.service.*;
import com.freeuni.quiz.repository.impl.ParticipantAnswerRepositoryImpl;
import com.freeuni.quiz.repository.impl.QuizCompletionRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class QuizAnswerHandler {

    private final QuizService quizService;
    private final ParticipantAnswerRepositoryImpl participantAnswerRepository;
    private final QuizCompletionRepositoryImpl quizCompletionRepository;

    public QuizAnswerHandler(QuizService quizService, 
                            ParticipantAnswerRepositoryImpl participantAnswerRepository,
                            QuizCompletionRepositoryImpl quizCompletionRepository) {
        this.quizService = quizService;
        this.participantAnswerRepository = participantAnswerRepository;
        this.quizCompletionRepository = quizCompletionRepository;
    }

    public void handleAnswerSubmission(HttpServletRequest request, HttpServletResponse response, 
                                      UserDTO currentUser, Long quizId) 
            throws IOException {
        
        String answer = request.getParameter("answer");
        if (answer != null && !answer.trim().isEmpty()) {
            saveAnswerToDatabase(request, currentUser, quizId, answer);
        }
        
        String nextAction = request.getParameter("nextAction");
        if ("next".equals(nextAction)) {
            response.sendRedirect("quiz-session?quizId=" + quizId + "&action=next");
        } else if ("review".equals(nextAction)) {
            response.sendRedirect("quiz-session?quizId=" + quizId + "&action=review");
        } else {
            response.sendRedirect("quiz-session?quizId=" + quizId);
        }
    }

    public void handleQuizCompletion(HttpServletRequest request, HttpServletResponse response, 
                                    UserDTO currentUser, Long quizId) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        String currentAnswer = request.getParameter("answer");
        if (currentAnswer != null && !currentAnswer.trim().isEmpty()) {
            saveAnswerToDatabase(request, currentUser, quizId, currentAnswer);
        }
        
        Long quizStartTime = (Long) session.getAttribute("quizStartTime");
        LocalDateTime startedAt;
        int totalTimeSeconds;
        
        if (quizStartTime != null) {
            long secondsAgo = (System.currentTimeMillis() - quizStartTime) / 1000;
            startedAt = LocalDateTime.now().minusSeconds(secondsAgo);
            totalTimeSeconds = (int) secondsAgo;
            System.out.println("DEBUG: Quiz completion - Total seconds: " + totalTimeSeconds + " for user " + currentUser.getId());
        } else {
            System.out.println("DEBUG: WARNING - No quiz start time found for user " + currentUser.getId());
            startedAt = LocalDateTime.now().minusMinutes(10);
            totalTimeSeconds = 600;
        }
        
        List<ParticipantAnswer> allAnswers = participantAnswerRepository.getAllAnswers((long) currentUser.getId(), quizId);
        
        double totalScore = 0.0;
        double maxPossibleScore = 0.0;
        
        List<Question> questions = quizService.getQuizQuestions(quizId);
        for (Question question : questions) {
            maxPossibleScore += question.getPoints();
        }
        
        for (ParticipantAnswer answer : allAnswers) {
            totalScore += answer.getPointsEarned();
        }
        
        QuizCompletion completion = new QuizCompletion(
            (long) currentUser.getId(),
            quizId,
            totalScore,
            maxPossibleScore,
            startedAt,
            totalTimeSeconds
        );
        
        completion.setFinishedAt(LocalDateTime.now());
        
        Long completionId = quizCompletionRepository.saveCompletion(completion);
        
        if (completionId != null) {
            cleanupSession(session, questions.size());
            
            response.sendRedirect("quiz-view?quizId=" + quizId + "&completed=true&score=" + totalScore + "&maxScore=" + maxPossibleScore);
        } else {
            request.setAttribute("errorMessage", "Failed to save quiz completion. Please try again.");
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
        }
    }

    private void saveAnswerToDatabase(HttpServletRequest request, UserDTO currentUser, Long quizId, String answer) {
        try {
            HttpSession session = request.getSession();
            Integer questionIndex = (Integer) session.getAttribute("currentQuestionIndex");
            if (questionIndex == null) questionIndex = 0;
            
            List<Question> questions = quizService.getQuizQuestions(quizId);
            if (questionIndex >= questions.size()) return;
            
            Question currentQuestion = questions.get(questionIndex);
            
            double pointsEarned = calculatePointsEarned(currentQuestion, answer);
            
            Long questionStartTime = (Long) session.getAttribute("questionStartTime_" + questionIndex);
            int timeSpentSeconds = 0;
            if (questionStartTime != null) {
                timeSpentSeconds = (int) ((System.currentTimeMillis() - questionStartTime) / 1000);
            }
            
            ParticipantAnswer participantAnswer = new ParticipantAnswer(
                (long) currentUser.getId(),
                quizId,
                (long) (questionIndex + 1),
                pointsEarned,
                timeSpentSeconds,
                answer
            );
            
            boolean saved = participantAnswerRepository.saveAnswer(participantAnswer);
            if (!saved) {
                System.err.println("Failed to save answer to database for user " + currentUser.getId() + 
                                 ", quiz " + quizId + ", question " + (questionIndex + 1));
            }
            
            saveAnswerToSession(request, questionIndex, answer);
            
        } catch (Exception e) {
            System.err.println("Error saving answer to database: " + e.getMessage());
        }
    }

    private double calculatePointsEarned(Question question, String answer) {
        if (question.getQuestionHandler() != null) {
            try {
                Map<String, String> userInputs = new HashMap<>();
                
                if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                    try {
                        int choiceIndex = Integer.parseInt(answer);
                        Map<String, Object> viewData = question.getQuestionHandler().getViewData();
                        @SuppressWarnings("unchecked")
                        List<String> options = (List<String>) viewData.get("choiceOptions");
                        if (options != null && choiceIndex >= 0 && choiceIndex < options.size()) {
                            userInputs.put("choiceSelection", options.get(choiceIndex));
                        }
                    } catch (NumberFormatException e) {
                        userInputs.put("choiceSelection", answer);
                    }
                } else {
                    userInputs.put("textInput", answer);
                }
                
                double score = question.getQuestionHandler().evaluateUserResponse(userInputs);
                return score * question.getPoints();
            } catch (Exception e) {
                System.err.println("Error evaluating answer: " + e.getMessage());
                return 0.0;
            }
        }
        return 0.0;
    }

    private void saveAnswerToSession(HttpServletRequest request, Integer questionIndex, String answer) {
        Map<Integer, String> answers = getSessionAnswers(request);
        answers.put(questionIndex, answer);
        request.getSession().setAttribute("quizAnswers", answers);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> getSessionAnswers(HttpServletRequest request) {
        Map<Integer, String> answers = (Map<Integer, String>) request.getSession().getAttribute("quizAnswers");
        if (answers == null) {
            answers = new HashMap<>();
            request.getSession().setAttribute("quizAnswers", answers);
        }
        return answers;
    }

    private void cleanupSession(HttpSession session, int questionCount) {
        session.removeAttribute("quizAnswers");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("quizStartTime");
        
        for (int i = 0; i < questionCount; i++) {
            session.removeAttribute("questionStartTime_" + i);
        }
    }
} 