package com.mastermind.services;

import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;

/**
 * Factory for creating Game instances with random number generation.
 * Handles the complexity of answer generation via external API.
 */
public class GameFactory {
    private final RandomNumberApiClient apiClient;

    public GameFactory(RandomNumberApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Game createGame(Player player) {
        NumCombination answer = apiClient.getRandomNums();
        return new Game(player, answer);
    }
}