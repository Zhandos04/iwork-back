package com.example.iwork.exceptions;

public class GeminiApiException extends RuntimeException {
    public GeminiApiException(String message) {
        super(message);
    }
}