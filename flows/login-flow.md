# Login Flow

## Endpoint

`POST /v1/api/auth/login`

## Data Flow

1. Client sends a request to `POST /v1/api/auth/login`
2. Spring routes the request to [AuthController.java](/server/src/main/java/com/portfolio/expensetracker/controller/AuthController.java)
3. The JSON body is mapped into `LoginRequest`
4. Validation runs through `@Valid @RequestBody`
5. If validation passes, `AuthController.login()` calls `authService.login(request)`
6. `AuthService.login(...)` normalizes the email
7. `UserRepository.findByEmailIgnoreCase(...)` loads the user from the database
8. If no user is found, `ApiException(HttpStatus.UNAUTHORIZED, ...)` is thrown
9. `PasswordEncoder.matches(...)` compares the raw password with `user.getPasswordHash()`
10. If the password does not match, `ApiException(HttpStatus.UNAUTHORIZED, ...)` is thrown
11. If the password matches, `JwtService.generateToken(user)` creates a JWT
12. The user and token are mapped into `LoginResponse`
13. The controller returns `200 OK`

## Files Involved

- [AuthController.java](/server/src/main/java/com/portfolio/expensetracker/controller/AuthController.java)
- [LoginRequest.java](/server/src/main/java/com/portfolio/expensetracker/dto/auth/LoginRequest.java)
- [AuthService.java](/server/src/main/java/com/portfolio/expensetracker/service/AuthService.java)
- [UserRepository.java](/server/src/main/java/com/portfolio/expensetracker/repository/UserRepository.java)
- [JwtService.java](/server/src/main/java/com/portfolio/expensetracker/service/JwtService.java)
- [LoginResponse.java](/server/src/main/java/com/portfolio/expensetracker/dto/auth/LoginResponse.java)
- [GlobalExceptionHandler.java](/server/src/main/java/com/portfolio/expensetracker/exception/GlobalExceptionHandler.java)

## Business Rules

- Email is trimmed and converted to lowercase before lookup
- Login failure always returns `401 Unauthorized`
- The same message is used for unknown email and wrong password
- JWT contains the user email in `subject`
- JWT also contains the user role in a custom `role` claim

## Request Example

```json
{
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
  "email": "demo@example.com",
  "role": "USER",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

## Short Flow

```text
Client
-> AuthController.login()
-> LoginRequest
-> AuthService.login()
-> UserRepository.findByEmailIgnoreCase()
-> PasswordEncoder.matches()
-> JwtService.generateToken()
-> LoginResponse
-> HTTP 200 OK
```
