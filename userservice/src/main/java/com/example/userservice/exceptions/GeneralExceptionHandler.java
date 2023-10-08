package com.example.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(CancelRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMyCustomException(CancelRequestException ex) {
        return ex.getMessage();
    }
}
