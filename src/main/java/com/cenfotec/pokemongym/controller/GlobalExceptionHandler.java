package com.cenfotec.pokemongym.controller;

import com.cenfotec.pokemongym.DTO.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage(ex.getMessage());
        responseDTO.setSuccess(false);
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        ResponseDTO responseDTO = new ResponseDTO();
        exception.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));

        responseDTO.setMessage(errors);
        responseDTO.setSuccess(false);
        return ResponseEntity.badRequest().body(responseDTO);
    }
}
