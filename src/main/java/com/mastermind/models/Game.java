package com.mastermind.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    Game(Player player, NumCombination answer) {
        this.status = Status.PENDING;
        this.player = player;
        this.answer = answer;
        this.maxAttempts = 10;
        this.guesses = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    public void start(){
       if (this.status == Status.PENDING) {
           this.status = Status.IN_PROGRESS;
       }
    }

    public Feedback playerGuess(NumCombination guess){
        if (guess == null) {
            throw new IllegalArgumentException("guess is null");
        }
        this.guesses.add(guess);

        Feedback feedback = Feedback.create(this.answer, guess);
        this.feedbacks.add(feedback);

        // Check win condition
        if (feedback.getCorrectPositions() == this.answer.getExpectedSize()) {
            this.status = Status.WON;
            // Check lose condition
        } else if (this.guesses.size() == this.maxAttempts) {
            this.status = Status.LOST;
        }
        // else keep playing

        return feedback;
    }

    public List<History> getHistory() {
        List<History> historyList = new ArrayList<>();

        for (int i = 0; i < guesses.size(); i++) {
            historyList.add(new History(guesses.get(i), feedbacks.get(i)));
        }

        return historyList;
    }
}
