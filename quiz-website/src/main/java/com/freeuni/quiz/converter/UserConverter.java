package com.freeuni.quiz.converter;

import com.freeuni.quiz.DTO.UserDTO;
import com.freeuni.quiz.bean.User;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImageURL(),
                user.getBio(),
                user.isAdmin()
        );
    }

    public static UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImageURL(),
                user.getBio(),
                user.isAdmin()
        );
    }
}
