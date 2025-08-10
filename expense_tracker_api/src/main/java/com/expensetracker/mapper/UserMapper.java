package com.expensetracker.mapper;

import com.expensetracker.dto.UserRegisterRequest;
import com.expensetracker.dto.UserResponse;
import com.expensetracker.model.User;

public final class UserMapper {

    private UserMapper() {

    }

    public static User toEntity(UserRegisterRequest request, String encodedPassword) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(encodedPassword);
        user.setActive(true);
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setActive(Boolean.TRUE.equals(user.getActive()));
        return response;
    }
}
