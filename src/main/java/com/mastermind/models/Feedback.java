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

        int correctPositions = countCorrectPositions(answer, guess);
        int correctDigits = countCorrectDigits(answer, guess);

        return new Feedback(correctDigits, correctPositions, answer.getExpectedSize());
    }

    /**
     * Counts the number of digits that match both value and position.
     */
    private static int countCorrectPositions(NumCombination answer, NumCombination guess) {
        int correctPositions = 0;
        List<Integer> answerNumbers = answer.getNumbers();
        List<Integer> guessNumbers = guess.getNumbers();

        for (int i = 0; i < answerNumbers.size(); i++) {
            if (answerNumbers.get(i).equals(guessNumbers.get(i))) {
                correctPositions++;
            }
        }

        return correctPositions;
    }

    /**
     * Counts the total number of correct digits, including both exact position matches
     * and digits that exist in both sequences but in different positions.
     */
    private static int countCorrectDigits(NumCombination answer, NumCombination guess) {
        Map<Integer, Integer> answerFreqCounter = toFreqCounter(answer.getNumbers());
        Map<Integer, Integer> guessFreqCounter = toFreqCounter(guess.getNumbers());

        // Remove exact position matches from the frequency counters to avoid double counting
        removePositionMatches(answer, guess, answerFreqCounter, guessFreqCounter);

        // Count remaining digit matches (correct digit, different position)
        int digitOnlyMatches = countDigitOnlyMatches(answerFreqCounter, guessFreqCounter);
        int exactPositionMatches = countCorrectPositions(answer, guess);

        return digitOnlyMatches + exactPositionMatches;
    }

    /**
     * Removes exact position matches from frequency counters to prevent double counting.
     * This ensures digits counted as position matches are not also counted as digit-only matches.
     */
    private static void removePositionMatches(NumCombination answer, NumCombination guess,
                                              Map<Integer, Integer> answerFreqCounter,
                                              Map<Integer, Integer> guessFreqCounter) {
        List<Integer> answerNumbers = answer.getNumbers();
        List<Integer> guessNumbers = guess.getNumbers();

        for (int i = 0; i < answerNumbers.size(); i++) {
            Integer answerDigit = answerNumbers.get(i);
            Integer guessDigit = guessNumbers.get(i);

            if (answerDigit.equals(guessDigit)) {
                answerFreqCounter.put(answerDigit, answerFreqCounter.get(answerDigit) - 1);
                guessFreqCounter.put(guessDigit, guessFreqCounter.get(guessDigit) - 1);
            }
        }
    }

    /**
     * Counts digits that appear in both sequences but in different positions.
     * Uses frequency counters that have already had position matches removed.
     */
    private static int countDigitOnlyMatches(Map<Integer, Integer> answerFreqCounter,
                                             Map<Integer, Integer> guessFreqCounter) {
        int digitOnlyMatches = 0;

        for (Map.Entry<Integer, Integer> entry : answerFreqCounter.entrySet()) {
            Integer digit = entry.getKey();
            Integer answerCount = entry.getValue();
            Integer guessCount = guessFreqCounter.getOrDefault(digit, 0);
            digitOnlyMatches += Math.min(answerCount, guessCount);
        }

        return digitOnlyMatches;
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

    private static Map<Integer, Integer> toFreqCounter(List<Integer> numList){
        Map<Integer, Integer> freqCounter = new HashMap<>();
        for (int num : numList) {
            freqCounter.put(num, freqCounter.getOrDefault(num, 0) + 1);
        }

        return freqCounter;
    }
}
