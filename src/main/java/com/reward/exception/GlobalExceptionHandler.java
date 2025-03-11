package com.reward.exception;

import com.reward.responsemodel.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseModel<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.NOT_FOUND.value(), "Error", ex.getMessage(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseModel<String>> handleBadRequest(BadRequestException ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.BAD_REQUEST.value(), "Error", ex.getMessage(), null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseModel<String>> handleUnauthorized(UnauthorizedException ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.UNAUTHORIZED.value(), "Error", ex.getMessage(), null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseModel<String>> handleGenericException(Exception ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "An unexpected error occurred", null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
