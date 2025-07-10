package com.freeuni.quiz.service;

import com.freeuni.quiz.DAO.MessageDAO;
import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.Message;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

public class MessageService {
    private final MessageDAO messageDAO;
    private final UserService userService;

    public MessageService(DataSource dataSource) {
        this.messageDAO = new MessageDAO(dataSource);
        this.userService = new UserService(dataSource);
    }

    public LinkedHashMap<Message, UserDTO> getConversationsWithProfileDetails(int userId) throws SQLException {
        List<Message> messages = messageDAO.getLatestConversations(userId);

        Set<Integer> otherUserIds = new HashSet<>();
        for (Message msg : messages) {
            int otherId = msg.getReceiverId() == userId ? msg.getSenderId() : msg.getReceiverId();
            otherUserIds.add(otherId);
        }

        Map<Integer, UserDTO> usersById = userService.findUsersByIds(otherUserIds);

        LinkedHashMap<Message, UserDTO> result = new LinkedHashMap<>();
        for (Message msg : messages) {
            int otherId = msg.getReceiverId() == userId ? msg.getSenderId() : msg.getReceiverId();
            UserDTO user = usersById.get(otherId);
            result.put(msg, user);
        }

        return result;
    }

}
