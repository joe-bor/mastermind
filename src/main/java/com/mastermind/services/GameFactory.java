package com.mastermind.services;

import com.mastermind.models.Difficulty;
import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;
import lombok.RequiredArgsConstructor;

/**
 * Factory for creating Game instances with configurable difficulty levels.
 * 
 * <p>This factory abstracts the complexity of generating secret combinations
 * using external random number services with appropriate retry logic and fallbacks.
 * Each game is created with a difficulty-appropriate secret combination.
 */
@RequiredArgsConstructor
public class GameFactory {
    private final NumberGenerator numberGenerator;

    public Game createGame(Player player) {
       return createGame(player, Difficulty.NORMAL);
    }

    /**
     * Creates a new game with the specified difficulty level.
     * 
     * <p>The secret combination is generated according to the difficulty constraints:
     * <ul>
     *   <li>EASY (3 digits, 0-5)</li>
     *   <li>NORMAL (4 digits, 0-7)</li>
     *   <li>HARD (5 digits, 0-9).</li>
     * <ul/>
     *
     * @param player the player who will participate in the game
     * @param difficulty the difficulty level that determines game constraints
     * @return a new Game instance configured for the specified difficulty
     */
    public Game createGame(Player player, Difficulty difficulty) {
        NumCombination answer =
                numberGenerator.generateNumbers(difficulty.getCombinationSize(), difficulty.getMaxRange());

        Game newGame = new Game(player, answer);
        newGame.setDifficulty(difficulty);

        return newGame;
    }
}