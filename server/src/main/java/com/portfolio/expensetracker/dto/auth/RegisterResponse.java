package com.portfolio.expensetracker.dto.auth;

import com.portfolio.expensetracker.entity.User;
import com.portfolio.expensetracker.entity.UserRole;
import java.time.Instant;
import java.time.LocalDate;

public record RegisterResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthday,
        String job,
        String email,
        UserRole role,
        Instant createdAt
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthday(),
                user.getJob(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
