package com.mastermind.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single game session of Mastermind.
 * 
 * <p>This class manages the complete game state including player information,
 * the secret combination, game progression, and difficulty settings.
 *
 * <ul>
 *   <li>Player information ({@link Player})</li>
 *   <li>The secret number combination to guess ({@link NumCombination})</li>
 *   <li>The history of guesses and their corresponding feedback ({@link Feedback})</li>
 *   <li>The game state ({@link Status}) and the maximum number of allowed attempts</li>
 *   <li>Difficulty level ({@link Difficulty}) that determines game constraints</li>
 * </ul>
 * 
 * <p>Games follow a strict lifecycle: PENDING → IN_PROGRESS → (WON|LOST)
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
    private int hintCount;
    private Difficulty difficulty;

    /**
     * Creates a new game with the specified player and secret combination.
     * 
     * @param player the player participating in this game
     * @param answer the secret combination to be guessed
     * @throws IllegalArgumentException if player or answer is null
     */
    public Game(Player player, NumCombination answer) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        
        this.status = Status.PENDING;
        this.player = player;
        this.answer = answer;
        this.maxAttempts = 10;
        this.guesses = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.hintCount = 2;
    }

    public void start(){
       switch (this.status) {
           case PENDING -> this.status = Status.IN_PROGRESS;
           case IN_PROGRESS, WON, LOST -> throw new IllegalStateException("Game already started");
       }
    }

    public Feedback playerGuess(NumCombination guess){
        if (guess == null) {
            throw new IllegalArgumentException("guess is null");
        }

        if (this.status != Status.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
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

    public int getRemainingAttempts() {
        return this.maxAttempts - this.guesses.size();
    }

    public String getHint() {
        if (this.hintCount == 0){
            return "No more hints left";
        }

        int size = this.answer.getNumbers().size();
        Random random = new Random();
        this.hintCount--;

        return this.answer.getNumbers()
                .get(random.nextInt(size))
                .toString();
    }
}
