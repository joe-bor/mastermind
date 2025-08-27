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
}