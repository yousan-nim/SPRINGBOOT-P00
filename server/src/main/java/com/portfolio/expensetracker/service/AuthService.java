package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.dto.auth.LoginRequest;
import com.portfolio.expensetracker.dto.auth.LoginResponse;
import com.portfolio.expensetracker.dto.auth.RegisterRequest;
import com.portfolio.expensetracker.dto.auth.RegisterResponse;
import com.portfolio.expensetracker.entity.User;
import com.portfolio.expensetracker.entity.UserRole;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setBirthday(request.birthday());
        user.setJob(request.job().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        return RegisterResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Email or password"));

        boolean matches = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!matches) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Email or password");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.from(user, token);
    }
}
