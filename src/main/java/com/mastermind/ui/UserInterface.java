package com.mastermind.ui;

import com.mastermind.models.Feedback;
import com.mastermind.models.NumCombination;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Responsible for handling user interactions, displaying messages and game state.
 */
public class UserInterface {
    private static final Scanner SCANNER = new Scanner(System.in);

    public void displayWelcomeMessage() {
        System.out.println("***** Welcome to Mastermind-CLI *****");
    }

    private void promptForGuess() {}

    private void displayFeedback() {}

    private void displayGameHistory() {}

    private void displayGameResults(){}

    private void promptForNewGame() {}
}
