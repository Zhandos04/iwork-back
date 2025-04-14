package com.example.iwork.exceptions;

import com.example.iwork.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response<?>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.NOT_ACCEPTABLE.value());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Response<?>> handleValidationException(ValidationException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleRuntimeException(Exception ex) {
        String errorMsg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
        Response<?> response = new Response<>(null, errorMsg, "Unexpected error occurred : " + errorMsg, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<?>> handleRuntimeException(RuntimeException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Response<?>> handleUserNotFoundException(UsernameNotFoundException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<?>> handleBadCredentialsException(BadCredentialsException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<?>> handleAccessDeniedException(AccessDeniedException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(IncorrectCodeException.class)
    public ResponseEntity<Response<?>> handleIncorrectCodeException(IncorrectCodeException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response<?>> handleInvalidTokenException(InvalidTokenException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Response<?>> handleFileUploadException(FileUploadException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<Response<?>> handleFilterNotFoundException(CompanyNotFoundException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Response<?>> handleFilterNotFoundException(ReviewNotFoundException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SalaryNotFoundException.class)
    public ResponseEntity<Response<?>> handleSalaryNotFoundException(SalaryNotFoundException ex) {
        Response<?> response = new Response<>(null, null, ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
