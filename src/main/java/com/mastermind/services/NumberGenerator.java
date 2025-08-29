package com.mastermind.services;

import com.mastermind.models.NumCombination;

/**
 * Interface for generating random number combinations for Mastermind games.
 * Implementations may use external APIs, local generation, or hybrid approaches.
 */
public interface NumberGenerator {
    /**
     * Generates a valid NumCombination for use as a game answer.
     * Implementations should handle failures gracefully and always return a valid combination.
     * 
     * @return A valid NumCombination suitable for the game
     */
    NumCombination generateNumbers();

    /**
     * Generates a NumCombination with specified size and range constraints.
     * 
     * @param size the number of digits to generate
     * @param maxRange the maximum value (inclusive) for each digit
     * @return a valid NumCombination with the specified constraints
     */
    NumCombination generateNumbers(int size, int maxRange);
}