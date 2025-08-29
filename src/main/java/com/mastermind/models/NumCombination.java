package com.mastermind.models;

import com.mastermind.config.GameConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a sequence of numbers in the Mastermind game.
 * <p>
 * This class is used for both the answer and player guesses.
 */
@Data
public class NumCombination {
    private final List<Integer> numbers;
    private final int expectedSize;
    private final int minNum;
    private final int maxNum;

    public NumCombination(List<Integer> numbers) {
        this(numbers, GameConfig.DEFAULT_ANSWER_SIZE, GameConfig.DEFAULT_MIN_VALUE, GameConfig.DEFAULT_MAX_VALUE);
    }

    public NumCombination(List<Integer> numbers, int expectedSize, int minNum, int maxNum) {
        if (numbers == null) {
            throw new IllegalArgumentException("Numbers list cannot be null");
        }

        if (numbers.size() != expectedSize) {
            throw new IllegalArgumentException("Must have exactly " + expectedSize + " numbers, got " + numbers.size());
        }

        for (int num : numbers) {
            if (num < minNum || num > maxNum) {
                throw new IllegalArgumentException("Number " + num + " must be between " + minNum + "-" + maxNum);
            }
        }

        this.numbers = List.copyOf(numbers);
        this.expectedSize = expectedSize;
        this.minNum = minNum;
        this.maxNum = maxNum;
    }

    public static NumCombination parse(String input) {
        return parse(input, GameConfig.DEFAULT_ANSWER_SIZE, GameConfig.DEFAULT_MIN_VALUE, GameConfig.DEFAULT_MAX_VALUE);
    }

    /**
     * Parses a string input into a NumCombination with specified constraints.
     * 
     * @param input the string to parse (e.g., "1 2 3 4")
     * @param expectedSize the required number of digits
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return a new NumCombination instance
     * @throws IllegalArgumentException if input is invalid or doesn't meet constraints
     */
    public static NumCombination parse(String input, int expectedSize, int min, int max) {
        if (input == null) {
            throw new IllegalArgumentException("Input is null");
        }

        String cleanedInput = input.trim();
        if (cleanedInput.isBlank()) {
            throw new IllegalArgumentException("Cleaned input is blank");
        }

        String[] cleanedStrings = cleanedInput.split(Pattern.quote(" "));

        List<Integer> numbers = new ArrayList<>();
        for (String entry: cleanedStrings) {
            if (!entry.trim().isEmpty()) {
                try {
                    int num = Integer.parseInt(entry.trim());
                    numbers.add(num);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format: " + entry, e);
                }
            }
        }

        return new NumCombination(numbers, expectedSize, min, max);
    }

    @Override
    public String toString() {
        return String.join(" ", numbers.stream()
            .map(String::valueOf)
            .toArray(String[]::new));
    }
}
