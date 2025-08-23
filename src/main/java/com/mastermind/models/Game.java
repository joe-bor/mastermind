package com.mastermind.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a single game session of Mastermind.
 *
 * <ul>
 *   <li>Player information ({@link Player})</li>
 *   <li>The secret number combination to guess ({@link NumCombination})</li>
 *   <li>The history of guesses and their corresponding feedback ({@link Feedback})</li>
 *   <li>The game state ({@link Status}) and the maximum number of allowed attempts</li>
 * </ul>
 */
@Data
@NoArgsConstructor
public class Game {
    private Status status;
    private Player player;
    private NumCombination answer;
    private int maxAttempts;
    private List<NumCombination> guesses;
    private List<Feedback> feedbacks;

    public void start(){}

    public void end(){}

    public Feedback playerGuess(NumCombination guess){
        return new Feedback();
    }

    public Map<String, String> getHistory() {
        return Map.of("guess", "feedback");
    }
}
