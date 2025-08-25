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

    public static Feedback create(NumCombination answer, NumCombination guess) {
        if (answer == null || guess == null) {
            throw  new IllegalArgumentException("answer nor guess cannot be null");
        }

        if (answer.getExpectedSize() != guess.getExpectedSize()) {
            throw new IllegalArgumentException("There is a size mismatch between answer and guess");
        }

        if (answer.equals(guess)) {
            return new Feedback(guess.getExpectedSize(), guess.getExpectedSize());
        }

        // Walk the two lists and compare digits and their positions
        int correctDigitCounter = 0;
        int correctPositionCounter = 0;

        for (int i = 0; i < answer.getNumbers().size(); i++) {
            int answerEntry = answer.getNumbers().get(i);
            int guessEntry = guess.getNumbers().get(i);

            // if the two numbers on the same index are the same then the position is correct
            if (answerEntry == guessEntry) correctPositionCounter++;
        }

        Map<Integer, Integer> answerFreqCounter = toFreqCounter(answer.getNumbers());
        Map<Integer, Integer> guessFreqCounter = toFreqCounter(guess.getNumbers());

        // Remove position matches from frequency counters to avoid double counting
        for (int i = 0; i < answer.getNumbers().size(); i++) {
            int answerEntry = answer.getNumbers().get(i);
            int guessEntry = guess.getNumbers().get(i);

            if (answerEntry == guessEntry) {
                answerFreqCounter.put(answerEntry, answerFreqCounter.get(answerEntry) - 1);
                guessFreqCounter.put(guessEntry, guessFreqCounter.get(guessEntry) - 1);
            }
        }

        // Count remaining digit matches
        for (Map.Entry<Integer, Integer> entry : answerFreqCounter.entrySet()) {
            int digit = entry.getKey();
            int answerCount = entry.getValue();
            int guessCount = guessFreqCounter.getOrDefault(digit, 0);
            correctDigitCounter += Math.min(answerCount, guessCount);
        }

        // Add position matches to get total correct digits
        correctDigitCounter += correctPositionCounter;

        return new Feedback(correctDigitCounter, correctPositionCounter);
    }

    private static Map<Integer, Integer> toFreqCounter(List<Integer> numList){
        Map<Integer, Integer> freqCounter = new HashMap<>();
        for (int num : numList) {
            freqCounter.put(num, freqCounter.getOrDefault(num, 0) + 1);
        }

        return freqCounter;
    }
}
