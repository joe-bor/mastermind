package com.mastermind.models;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the feedback generated after a player's guess.
 * <p>
 * Contains information about:
 * <ul>
 *   <li>The number of correct digits, regardless of position</li>
 *   <li>The number of digits in the correct position</li>
 * </ul>
 */
@Data
public class Feedback {
    private final int correctDigits;
    private final int correctPositions;
    private final int expectedSize;

    public static Feedback create(NumCombination answer, NumCombination guess) {
        if (answer == null || guess == null) {
            throw  new IllegalArgumentException("answer nor guess cannot be null");
        }

        if (answer.getExpectedSize() != guess.getExpectedSize()) {
            throw new IllegalArgumentException("There is a size mismatch between answer and guess");
        }

        if (answer.equals(guess)) {
            int size = guess.getExpectedSize();
            return new Feedback(size, size, size);
        }

        int[] results = calculateFeedback(answer, guess);
        int correctDigits = results[0];
        int correctPositions = results[1];

        return new Feedback(correctDigits, correctPositions, answer.getExpectedSize());
    }


    /**
     * Counts both correct digits and correct positions in two passes.
     * Returns [correctDigits, correctPositions].
     */
    private static int[] calculateFeedback(NumCombination answer, NumCombination guess) {
        List<Integer> answerNumbers = answer.getNumbers();
        List<Integer> guessNumbers = guess.getNumbers();
        
        int correctPositions = 0;
        Map<Integer, Integer> answerFreqCounter = new HashMap<>();
        Map<Integer, Integer> guessFreqCounter = new HashMap<>();
        
        // First pass: count exact matches and build frequency counters for non-matches
        for (int i = 0; i < answerNumbers.size(); i++) {
            Integer answerDigit = answerNumbers.get(i);
            Integer guessDigit = guessNumbers.get(i);
            
            if (answerDigit.equals(guessDigit)) {
                correctPositions++;
            } else {
                // Only add to frequency counters if not an exact match
                answerFreqCounter.put(answerDigit, answerFreqCounter.getOrDefault(answerDigit, 0) + 1);
                guessFreqCounter.put(guessDigit, guessFreqCounter.getOrDefault(guessDigit, 0) + 1);
            }
        }
        
        // Second pass: count digit-only matches from frequency counters
        int digitOnlyMatches = 0;
        for (Map.Entry<Integer, Integer> entry : answerFreqCounter.entrySet()) {
            Integer digit = entry.getKey();
            Integer answerCount = entry.getValue();
            Integer guessCount = guessFreqCounter.getOrDefault(digit, 0);
            digitOnlyMatches += Math.min(answerCount, guessCount);
        }
        
        return new int[]{correctPositions + digitOnlyMatches, correctPositions};
    }


    @Override
    public String toString(){
        if (this.correctDigits == 0 && this.correctPositions == 0) {
            return "All incorrect";
        }

        if (this.correctPositions == this.expectedSize) {
            return "All correct";
        }

        return "%d correct numbers, and %d correct location".formatted(correctDigits, correctPositions);
    }

}
