/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.exceptions;

/**
 *
 * @author braya
 */
public class ApiException extends RuntimeException {
    private final int httpStatus;
 
    public ApiException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
 
    public ApiException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
 
    public int getHttpStatus() { return httpStatus; }
    
}
