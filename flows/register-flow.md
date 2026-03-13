# Register Flow

## Endpoint

`POST /v1/api/auth/register`

## Data Flow

1. Client sends a request to `POST /v1/api/auth/register`
2. Spring routes the request to [AuthController.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/controller/AuthController.java)
3. The JSON body is mapped into `RegisterRequest`
4. Validation runs through `@Valid @RequestBody`
5. If validation fails, `GlobalExceptionHandler` returns `ApiErrorResponse`
6. If validation passes, `AuthController.register()` calls `authService.register(request)`
7. `AuthService.register(...)` normalizes the email, checks duplicates, hashes the password, and builds a `User`
8. `UserRepository.existsByEmailIgnoreCase(...)` checks whether the email already exists
9. If the email already exists, `ApiException(HttpStatus.CONFLICT, ...)` is thrown
10. If the email is available, the new user is saved with `userRepository.save(user)`
11. JPA persists the entity into the `users` table
12. The saved entity is converted into `RegisterResponse`
13. The controller returns `201 Created`

## Files Involved

- [AuthController.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/controller/AuthController.java)
- [RegisterRequest.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/RegisterRequest.java)
- [AuthService.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/service/AuthService.java)
- [UserRepository.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/repository/UserRepository.java)
- [User.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/entity/User.java)
- [RegisterResponse.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/dto/auth/RegisterResponse.java)
- [SecurityConfig.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/config/SecurityConfig.java)
- [GlobalExceptionHandler.java](/Users/mac/Documents/GitHub/SPRINGBOOT-P00/server/src/main/java/com/portfolio/expensetracker/exception/GlobalExceptionHandler.java)

## Business Rules

- Email is trimmed and converted to lowercase before saving
- Duplicate email is rejected
- Password is hashed with `BCryptPasswordEncoder`
- New users are created with role `USER`

## Request Example

```json
{
  "firstName": "Demo",
  "lastName": "User",
  "birthday": "1999-05-20",
  "job": "Backend Developer",
  "email": "demo@example.com",
  "password": "password123"
}
```

## Response Example

```json
{
  "id": 1,
  "firstName": "Demo",
  "lastName": "User",
  "birthday": "1999-05-20",
  "job": "Backend Developer",
  "email": "demo@example.com",
  "role": "USER",
  "createdAt": "2026-03-12T00:00:00Z"
}
```

## Short Flow

```text
Client
-> AuthController.register()
-> RegisterRequest
-> AuthService.register()
-> UserRepository.existsByEmailIgnoreCase()
-> PasswordEncoder.encode()
-> UserRepository.save()
-> RegisterResponse
-> HTTP 201 Created
```
