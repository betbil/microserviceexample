package com.example.userservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler {

    @ExceptionHandler(CancelRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMyCustomException(CancelRequestException ex) {
        log.warn("CancelRequestException: {}", ex.getMessage());
        return ex.getMessage();
    }
}
