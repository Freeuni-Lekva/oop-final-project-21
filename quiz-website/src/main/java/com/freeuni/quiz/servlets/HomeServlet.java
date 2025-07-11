package com.freeuni.quiz.servlets;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.DTO.PopularQuizDTO;
import com.freeuni.quiz.DTO.QuizChallengeDTO;
import com.freeuni.quiz.bean.Quiz;
import com.freeuni.quiz.bean.QuizCompletion;
import com.freeuni.quiz.bean.FriendshipRequest;
import com.freeuni.quiz.bean.Message;
import com.freeuni.quiz.service.QuizService;
import com.freeuni.quiz.service.UserService;
import com.freeuni.quiz.service.QuizChallengeService;
import com.freeuni.quiz.service.FriendshipRequestService;
import com.freeuni.quiz.service.MessageService;
import com.freeuni.quiz.service.AnnouncementService;
import com.freeuni.quiz.DTO.AnnouncementDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private QuizService quizService;
    private UserService userService;
    private QuizChallengeService challengeService;
    private FriendshipRequestService friendRequestService;
    private MessageService messageService;
    private AnnouncementService announcementService;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        this.quizService = new QuizService(dataSource);
        this.userService = new UserService(dataSource);
        this.challengeService = new QuizChallengeService(dataSource);
        this.friendRequestService = new FriendshipRequestService(dataSource);
        this.messageService = new MessageService(dataSource);
        this.announcementService = new AnnouncementService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        UserDTO currentUser = (session != null) ? (UserDTO) session.getAttribute("user") : null;
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            List<PopularQuizDTO> popularQuizzes = quizService.getPopularQuizzesWithCompletionCount(10);
            List<Quiz> recentlyCreatedQuizzes = quizService.getRecentlyCreatedQuizzes(10);
            List<Quiz> userRecentCreatedQuizzes = quizService.getRecentlyCreatedByUser((long) currentUser.getId(), 10);
            List<QuizCompletion> userRecentCompletions = quizService.getRecentCompletionsByUser((long) currentUser.getId(), 10);
            List<QuizCompletion> friendsRecentCompletions = quizService.getRecentCompletionsByFriends((long) currentUser.getId(), 10);

            List<QuizChallengeDTO> recentChallenges = challengeService.getRecentReceivedChallenges(currentUser.getId(), 10, userService, quizService);
            List<FriendshipRequest> recentFriendRequests = friendRequestService.getRecentRequestsReceivedByUser(currentUser.getId(), 10);
            LinkedHashMap<Message, UserDTO> recentConversations = messageService.getRecentConversationsWithProfileDetails(currentUser.getId(), 10);
            List<AnnouncementDTO> recentAnnouncements = announcementService.getRecentAnnouncements(3);
            
            Map<Long, Quiz> quizMap = userRecentCompletions.stream()
                .map(completion -> quizService.getQuizById(completion.getTestId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Quiz::getId, quiz -> quiz, (existing, replacement) -> existing));

            Map<Long, Quiz> friendsQuizMap = friendsRecentCompletions.stream()
                .map(completion -> quizService.getQuizById(completion.getTestId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Quiz::getId, quiz -> quiz, (existing, replacement) -> existing));

            Map<Long, UserDTO> friendsUserMap = new HashMap<>();
            for (QuizCompletion completion : friendsRecentCompletions) {
                try {
                    UserDTO user = userService.findById(completion.getParticipantUserId().intValue());
                    if (user != null) {
                        friendsUserMap.put(completion.getParticipantUserId(), user);
                    }
                } catch (SQLException ignored) {
                }
            }

            Map<Integer, UserDTO> friendRequestSenders = new HashMap<>();
            for (FriendshipRequest friendRequest : recentFriendRequests) {
                try {
                    UserDTO sender = userService.findById(friendRequest.getRequestSenderId());
                    if (sender != null) {
                        friendRequestSenders.put(friendRequest.getRequestSenderId(), sender);
                    }
                } catch (SQLException ignored) {
                }
            }

            request.setAttribute("popularQuizzes", popularQuizzes);
            request.setAttribute("recentlyCreatedQuizzes", recentlyCreatedQuizzes);
            request.setAttribute("userRecentCreatedQuizzes", userRecentCreatedQuizzes);
            request.setAttribute("userRecentCompletions", userRecentCompletions);
            request.setAttribute("friendsRecentCompletions", friendsRecentCompletions);
            request.setAttribute("quizMap", quizMap);
            request.setAttribute("friendsQuizMap", friendsQuizMap);
            request.setAttribute("friendsUserMap", friendsUserMap);

            request.setAttribute("recentChallenges", recentChallenges);
            request.setAttribute("recentFriendRequests", recentFriendRequests);
            request.setAttribute("recentConversations", recentConversations);
            request.setAttribute("friendRequestSenders", friendRequestSenders);
            request.setAttribute("recentAnnouncements", recentAnnouncements);

            request.getRequestDispatcher("home.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading home page data: " + e.getMessage());
            request.getRequestDispatcher("home.jsp").forward(request, response);
        }
    }
} 