package com.example.iwork.controllers;

import com.example.iwork.config.CustomAuthenticationProvider;
import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.CodeDTO;
import com.example.iwork.dto.requests.LoginDTO;
import com.example.iwork.dto.requests.ResentCodeDTO;
import com.example.iwork.dto.requests.UserDTO;
import com.example.iwork.dto.responses.AuthDTO;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.IncorrectCodeException;
import com.example.iwork.exceptions.InvalidTokenException;
import com.example.iwork.exceptions.UserAlreadyExistsException;
import com.example.iwork.jwt.JwtService;
import com.example.iwork.services.TokenBlacklistService;
import com.example.iwork.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name="Auth", description="Взаймодействие с пользователями")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomAuthenticationProvider authenticationProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final ModelMapper modelMapper;

    @PostMapping("/signup")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by checking for existing IDs and phone numbers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Code sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input, validation failed", content = @Content),
            @ApiResponse(responseCode = "406", description = "User already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred (internal server error)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while sending the verification email", content = @Content)
    })
    public ResponseEntity<Response<?>> register(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) throws UserAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        userService.registerNewUser(userDTO);
        Response<String> response = new Response<>(null, "Код успешно отправлен!", null, HttpStatus.ACCEPTED.value());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify Email",
            description = "Verifies the reset code entered by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect reset code", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Response<?>> verifyEmail(@RequestBody CodeDTO codeDTO) {
        Optional<User> userOptional = userService.getUserByEmail(codeDTO.getEmail());

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        User user = userOptional.get();
        if (!user.getConfirmationCode().equals(codeDTO.getCode())) {
            throw new IncorrectCodeException("Неправильный код");
        }

        user.setEmailVerified(true);
        userService.update(user);
        Response<String> response = new Response<>(null, "Пользователь успешно зарегистрирован!", null, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/resendCode")
    @Operation(
            summary = "Resend code",
            description = "Resends the verification code to the user's email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code resent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the provided email", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
    })
    public ResponseEntity<Response<?>> resendCode(@RequestBody ResentCodeDTO resentCodeDTO) {
        userService.resentCode(resentCodeDTO.getEmail());
        Response<String> response = new Response<>(null, "Код успешно переотправлен!", null, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns an Auth token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, access token returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data or email format", content = @Content),
            @ApiResponse(responseCode = "401", description = "Incorrect email or password", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not verified yet", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
    })
    public ResponseEntity<Response<?>> login(@RequestBody LoginDTO loginDTO) {
        Optional<User> userOptional = userService.getUserByUsername(loginDTO.getUsername());

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Такой пользователь не существует");
        }

        User user = userOptional.get();


        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername1(), loginDTO.getPassword())
        );

        if (!user.getEmailVerified()) {
            throw new AccessDeniedException("Это пользователь еще не верифицирован!");
        }

        Map<String, String> tokens = jwtService.generateTokens(loginDTO.getUsername(), user.getRole().name());
        AuthDTO authDTO = modelMapper.map(user, AuthDTO.class);
        if (user.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("LLLL yyyy", new Locale("ru"));

            String formattedDate = formatter.format(user.getCreatedAt());

            authDTO.setWithUsSince(formattedDate.substring(0, 1).toUpperCase() +
                    formattedDate.substring(1));
        }
        authDTO.setAccessToken(tokens.get("accessToken"));
        authDTO.setRefreshToken(tokens.get("refreshToken"));

        Response<AuthDTO> response = new Response<>(authDTO, "Успешный вход", null, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Logs out the user by invalidating the current token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
    })
    public ResponseEntity<Response<?>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expirationTime = jwtService.extractExpiration(token);
            tokenBlacklistService.addTokenToBlacklist(token, expirationTime);
            Response<String> response = new Response<>(null, "Выход из системы успешно завершен!", null, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        }
        throw new InvalidTokenException("Невалидный токен!");
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token", description = "Refreshes the access token using a valid refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Invalid or expired refresh token"),
                    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
            },
            security = @SecurityRequirement(name = "bearerToken"))

    public ResponseEntity<Response<?>> refreshAccessToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String refreshToken = headerAuth.substring(7);
            try {
                if (jwtService.validateRefreshToken(refreshToken)) { // Проверка валидности рефреш токена
                    String userName = jwtService.extractUsername(refreshToken);
                    // Проверка, что пользователь существует и активен
                    UserDetails userDetails = userService.loadUserByUsername(userName);
                    User user = userService.getUserByUsername(userName).get();
                    if (userDetails != null && !jwtService.isTokenExpired(refreshToken)) {
                        String newAccessToken = jwtService.generateTokens(userName, user.getRole().name()).get("accessToken");
                        Map<String, String> tokens = new HashMap<>();
                        tokens.put("accessToken", newAccessToken);
                        tokens.put("refreshToken", refreshToken); // Отправляем тот же рефреш токен обратно
                        Response<Map<String, String>> response = new Response<>(tokens, "Токен обновлен", null, HttpStatus.OK.value());
                        return ResponseEntity.ok(response);
                    }
                }
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid refresh token");
            }
        }
        throw new InvalidTokenException("Invalid or expired refresh token");
    }
}