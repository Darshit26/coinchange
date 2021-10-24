package com.example.demo.config;

import com.example.demo.domain.Error;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApiControllerAdvise {

    @Autowired
    public ApiControllerAdvise() {
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        List<String> errorList = new ArrayList<>();
        errorList.add("Error in application. Please try again later.");
        Error error = new Error();
        error.setErrors(errorList);
        return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> exceptionHandler(HttpMessageNotReadableException ex) {
        List<String> errorList = new ArrayList<>();
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            errorList.add(String.format("Invalid request. value: %s is not acceptable for %s.", invalidFormatException.getValue().toString(), invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1)));
        }
        Error error = new Error();
        error.setErrors(errorList);
        return new ResponseEntity<Error>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
