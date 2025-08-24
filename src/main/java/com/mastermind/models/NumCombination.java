package com.mastermind.models;

import lombok.Data;

import java.util.List;

/**
 * Represents a sequence of numbers in the Mastermind game.
 * <p>
 * This class is used for both the answer and player guesses.
 */
@Data
public class NumCombination {
    private final List<Integer> numbers;
}
