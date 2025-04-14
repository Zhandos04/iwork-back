package com.example.iwork.exceptions;

public class SalaryNotFoundException extends RuntimeException {
    public SalaryNotFoundException(String msg){
        super(msg);
    }
}