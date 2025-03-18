package com.reward.exception;

import com.reward.responsemodel.ResponseModel;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseModel<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));

        ResponseModel<Map<String, String>> response = ResponseModel.error(400, "Validation Failed", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseModel<String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ResponseModel.error(413, "File size must not exceed 1MB",null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseModel<String>> handleAccessDenied(AccessDeniedException ex) {
        ResponseModel<String> response = ResponseModel.error(403, "Forbidden: You do not have permission to access this resource",null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

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

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ResponseModel<String>> handleUserAlreadyExists(AlreadyExistsException ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.CONFLICT.value(), "Error", ex.getMessage(), null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseModel<String>> handleGenericException(Exception ex) {
        ResponseModel<String> response = new ResponseModel<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "An unexpected error occurred", null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
