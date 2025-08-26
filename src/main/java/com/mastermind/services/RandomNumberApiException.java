package com.mastermind.services;

/**
 * Exception thrown when Random.org API encounters errors.
 * Provides specific context for different types of API failures.
 */
public class RandomNumberApiException extends RuntimeException {
    
    public RandomNumberApiException(String message) {
        super(message);
    }
    
    public RandomNumberApiException(String message, Throwable cause) {
        super(message, cause);
    }
}