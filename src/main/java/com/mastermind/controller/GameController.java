package com.mastermind.controller;

import com.mastermind.models.Feedback;
import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;
import com.mastermind.models.Status;
import com.mastermind.services.GameFactory;
import com.mastermind.ui.MenuChoice;
import com.mastermind.ui.UserInterface;
import lombok.RequiredArgsConstructor;

/**
 * Orchestrates the game flow between UI and Game components.
 * Coordinates user interactions, game state, and display logic.
 */
@RequiredArgsConstructor
public class GameController {
    private final UserInterface ui;
    private final GameFactory gameFactory;

    public void startGame() {
        ui.displayWelcomeMessage();
        
        boolean playAgain = true;
        while (playAgain) {
            try {
                Game game = createNewGame();
                game.start();
                gameLoop(game);
                
                playAgain = ui.promptForNewGame();
            } catch (Exception e) {
                ui.displayError("Failed to create game: " + e.getMessage());
                playAgain = ui.promptForNewGame();
            }
        }
    }

    private Game createNewGame() {
        // TODO: Ask for player name later; maybe add method in UI that displays message and captures input
        Player player = new Player("Player");
        return gameFactory.createGame(player);
    }

    private void gameLoop(Game game) {
        while (game.getStatus() == Status.IN_PROGRESS) {
            int userChoice = ui.displayGameMenu(game.getRemainingAttempts());
            MenuChoice choice = MenuChoice.fromValue(userChoice);
            
            switch (choice) {
                case MAKE_GUESS -> handleGuess(game);
                case SHOW_HISTORY -> ui.displayGameHistory(game.getHistory());
                case EXIT_GAME -> {
                    System.out.println("Game ended by player.");
                    return;
                }
                case null -> ui.displayError("Invalid menu choice. Please try again.");
            }
        }
    }

    private void handleGuess(Game game) {
        NumCombination guess = ui.promptForGuess(game.getRemainingAttempts());
        Feedback feedback = game.playerGuess(guess);
        ui.displayFeedback(guess, feedback);
        
        // Check if game ended after this guess
        if (game.getStatus() != Status.IN_PROGRESS) {
            ui.displayGameResults(game.getStatus(), game.getAnswer());
        }
    }
}