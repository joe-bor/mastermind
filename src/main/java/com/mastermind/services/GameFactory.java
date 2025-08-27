package com.mastermind.services;

import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;
import lombok.RequiredArgsConstructor;

/**
 * Factory for creating Game instances with random number generation.
 * Handles the complexity of answer generation via external API.
 */
@RequiredArgsConstructor
public class GameFactory {
    private final NumberGenerator numberGenerator;

    public Game createGame(Player player) {
        NumCombination answer = numberGenerator.generateNumbers();
        return new Game(player, answer);
    }
}