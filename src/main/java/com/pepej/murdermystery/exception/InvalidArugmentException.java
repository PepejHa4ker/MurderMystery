package com.pepej.murdermystery.exception;


public class InvalidArugmentException extends RuntimeException {

    public InvalidArugmentException(String message) {
        super(message);
    }

    public InvalidArugmentException() {
        super("Invalid argument");
    }


}
