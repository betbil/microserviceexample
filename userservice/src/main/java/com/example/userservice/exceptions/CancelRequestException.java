package com.example.userservice.exceptions;

public class CancelRequestException extends RuntimeException{
    public CancelRequestException(String message) {
        super(message);
    }
}
