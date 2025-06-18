package com.freeuni.quiz.converter;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.User;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImageURL(),
                user.getBio()
        );
    }
}
