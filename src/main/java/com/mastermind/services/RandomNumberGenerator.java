package com.mastermind.services;

import com.mastermind.models.NumCombination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Resilient number generator that attempts to use an external API with fallback to local generation.
 * Implements retry logic with exponential backoff for transient failures.
 */
public class RandomNumberGenerator implements NumberGenerator {
    private final RandomNumberApiClient apiClient;
    
    public RandomNumberGenerator(RandomNumberApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    @Override
    public NumCombination generateNumbers() {
        int maxAttempts = 3; // Original + 2 retries
        int delayMs = 1000; // 1-second base delay
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return apiClient.getRandomNums();
            } catch (RandomNumberApiException e) {
                if (attempt == maxAttempts) {
                    // All attempts failed, fall back to local random
                    System.out.println("API failed after " + maxAttempts + " attempts, using local random generation");
                    return generateLocalRandomNumbers();
                }
                
                // Wait before retry with exponential backoff
                try {
                    Thread.sleep(delayMs * attempt); // 1s, 2s delays
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    // If interrupted, fall back immediately
                    return generateLocalRandomNumbers();
                }
                
                System.out.println("API attempt " + attempt + " failed, retrying...");
            }
        }
        
        // This should never be reached, but fallback just in case
        return generateLocalRandomNumbers();
    }
    
    private NumCombination generateLocalRandomNumbers() {
        final int size = 4;
        final int maxValue = 8; // Exclusive upper bound for Random.nextInt()
        
        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            numbers.add(random.nextInt(maxValue)); // 0-7 range
        }
        
        return new NumCombination(numbers);
    }
}