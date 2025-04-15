package com.example.iwork.exceptions;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String s) {
        super(s);
    }
}
