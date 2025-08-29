package com.mastermind.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Represents the difficulty levels available in the Mastermind game.
 * Each difficulty level defines the size of the secret combination and the range of possible numbers.
 */
@RequiredArgsConstructor
@Getter
public enum Difficulty {
    /** Easy mode: 3-digit combination with numbers 0-5 */
    EASY(1, 3, 5),
    
    /** Normal mode: 4-digit combination with numbers 0-7 (classic Mastermind) */
    NORMAL(2, 4, 7),
    
    /** Hard mode: 5-digit combination with numbers 0-9 */
    HARD(3, 5, 9);

    /** The numeric value used for menu selection */
    private final int value;
    
    /** The number of positions in the secret combination */
    private final int combinationSize;
    
    /** The maximum number that can appear in the combination (0-based, inclusive) */
    private final int maxRange;

    /**
     * Converts a numeric value to its corresponding Difficulty enum.
     * 
     * @param value the numeric value (1=EASY, 2=NORMAL, 3=HARD)
     * @return the matching Difficulty enum, or null if no match found
     */
    public static Difficulty fromValue(int value) {
        return Arrays.stream(values())
                .filter(difficulty -> difficulty.value == value)
                .findFirst()
                .orElse(null);
    }
}
