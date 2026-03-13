# Auth Implementation Summary

## What Has Been Implemented

The project now has the base pieces for authentication with Spring Security and JWT.

## Completed Parts

### 1. User Registration

- Endpoint: `POST /v1/api/auth/register`
- Creates a new user in PostgreSQL
- Hashes passwords with `BCryptPasswordEncoder`
- Rejects duplicate email addresses
- Stores user profile fields:
  - `firstName`
  - `lastName`
  - `birthday`
  - `job`
  - `email`
  - `passwordHash`
  - `role`

Main files:

- [User.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/entity/User.java)
- [RegisterRequest.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/RegisterRequest.java)
- [RegisterResponse.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/RegisterResponse.java)
- [AuthService.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/AuthService.java)

### 2. Login

- Endpoint: `POST /v1/api/auth/login`
- Finds users by email
- Verifies the password with `passwordEncoder.matches(...)`
- Returns JWT access token in the response

Main files:

- [LoginRequest.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/LoginRequest.java)
- [LoginResponse.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/LoginResponse.java)
- [AuthController.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/controller/AuthController.java)
- [AuthService.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/AuthService.java)

### 3. JWT Service

`JwtService` is responsible for:

- generating JWT tokens
- extracting email from a token
- validating the token expiration and owner

Current methods:

- `generateToken(User user)`
- `extractEmail(String token)`
- `isTokenValid(String token, UserDetails userDetails)`

Main file:

- [JwtService.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/JwtService.java)

### 4. Spring Security UserDetails Integration

To make JWT authentication work with Spring Security, the project now includes:

- `UserPrincipal`
- `CustomUserDetailsService`

Purpose:

- load users from the database through `UserRepository`
- expose user data in the `UserDetails` format expected by Spring Security

Main files:

- [UserPrincipal.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/UserPrincipal.java)
- [CustomUserDetailsService.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/CustomUserDetailsService.java)

### 5. JWT Filter

`JwtAuthenticationFilter` has been added to:

- read the `Authorization` header
- extract the bearer token
- extract the email from JWT
- load user details
- validate the token
- place authentication into `SecurityContextHolder`

Main file:

- [JwtAuthenticationFilter.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/config/JwtAuthenticationFilter.java)

### 6. Security Configuration

`SecurityConfig` has been updated to:

- disable CSRF
- use CORS
- use stateless session policy
- allow public access to auth and health endpoints
- prepare JWT filter for the security chain

Main file:

- [SecurityConfig.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/config/SecurityConfig.java)

## Current Auth Flow

```text
Register:
Client
-> AuthController.register()
-> AuthService.register()
-> UserRepository
-> PostgreSQL

Login:
Client
-> AuthController.login()
-> AuthService.login()
-> UserRepository.findByEmailIgnoreCase()
-> PasswordEncoder.matches()
-> JwtService.generateToken()
-> LoginResponse

Protected Request:
Client sends Authorization: Bearer <token>
-> JwtAuthenticationFilter
-> JwtService.extractEmail()
-> CustomUserDetailsService.loadUserByUsername()
-> JwtService.isTokenValid()
-> SecurityContextHolder.setAuthentication(...)
-> Controller
```

## What Comes Next

Suggested next steps:

1. finish and verify `SecurityConfig` route protection
2. test protected endpoints with and without token
3. bind transactions and categories to the authenticated user
4. add `GET /v1/api/auth/me`
5. optionally add refresh token support later
