/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.exceptions;

/**
 *
 * @author braya
 */
public class ValidationException extends RuntimeException {
     private int code;

    public ValidationException(String message) {
        super(message);
        this.code = 400;
    }

    public int getCode() {
        return code;
    }
}
