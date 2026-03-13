package com.portfolio.expensetracker.dto.auth;

import com.portfolio.expensetracker.entity.User;
import com.portfolio.expensetracker.entity.UserRole;

public record LoginResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        String accessToken,
        String tokenType) {

    public static LoginResponse from(User user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                token,
                "Bearer");
    }
}