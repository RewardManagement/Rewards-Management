package com.reward.responsemodel;

import lombok.Data;

@Data
public class ResponseModel<T> {
    private int statusCode;
    private String status;
    private String message;
    private T response;

    public ResponseModel(int statusCode, String status, String message, T response) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.response = response;
    }

    public static <T> ResponseModel<T> success(int statusCode, String message, T response) {
        return new ResponseModel<>(statusCode, "Success", message, response);
    }

    public static <T> ResponseModel<T> error(int statusCode, String message, T response) {
        return new ResponseModel<>(statusCode, "Error", message, response);
    }
    
}
