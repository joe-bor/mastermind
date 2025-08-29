package com.mastermind.ui;

import com.mastermind.models.Feedback;
import com.mastermind.models.History;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Status;

import java.util.List;
import java.util.Scanner;

/**
 * Responsible for handling user inputs, displaying messages and game state.
 */
public class UserInterface {
    private static final Scanner SCANNER = new Scanner(System.in);

    // -- Input --
    public NumCombination promptForGuess(int remainingAttempts, int expectedSize, int minValue, int maxValue) {
        while (true) {
            System.out.printf("Enter your guess (%d attempts remaining): ", remainingAttempts);
            String input = SCANNER.nextLine().trim();

            try {
                return NumCombination.parse(input, expectedSize, minValue, maxValue);
            } catch (IllegalArgumentException e) {
                System.out.printf("""
                    
                    *** INVALID INPUT ***
                    %s
                    
                    => Please enter %d numbers between %d-%d, separated by spaces
                    
                    """, e.getMessage(), expectedSize, minValue, maxValue);
            }
        }
    }

    public boolean promptForNewGame() {
        while (true) {
            System.out.print("Would you like to play again? (y/n): ");
            String input = SCANNER.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("\n--- Please enter 'y' for yes or 'n' for no ---\n");
            }
        }
    }

    public String promptForPlayerName() {
        String name;

        do {
            System.out.print("What is your name?: ");
            name = SCANNER.nextLine().trim();
        } while (name.isBlank());

        return name;
    }

    public int promptForDifficultyLevel() {
        while (true) {
            System.out.print("""
                    -----------------------------
                    Pick Difficulty Level
                    -----------------------------
                    1. Easy: 3 digits, numbers 0-5
                    2. Normal: 4 digits, numbers 0-7
                    3. Hard: 5 digits, numbers 0-9
                    
                    Enter your choice (1-3):\s """);

            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n*** Invalid input. Please enter a number. ***\n");
            }
        }
    }


    // -- Display --
    public void displayWelcomeMessage() {
        System.out.print("""
            ***** Welcome to Mastermind-CLI *****
            - Guess the secret number combination!
            - Choose your difficulty level to determine game constraints.
            - You have 10 attempts to crack the code.
            
            """);
    }

    public int displayGameMenu(String name, int remainingAttempts, int menuSize) {
        while (true) {
            System.out.printf("""
                
                ========================================
                           MASTERMIND - GAME MENU
                Player: %s
                ========================================
                Remaining attempts: %d
                
                Choose an option:
                1. Make a guess
                2. Show game history
                3. Exit game
                4. Use a Hint :)
                
                Enter your choice (1-%d):\s """, name, remainingAttempts, menuSize);
            
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n*** Invalid input. Please enter a number. ***\n");
            }
        }
    }

    public void displayFeedback(NumCombination guess, Feedback feedback) {
        System.out.printf("""
            Your guess: %s
            Result: %s
            
            """, guess.toString(), feedback.toString());
    }

    public void displayGameHistory(List<History> history) {
        if (history.isEmpty()) {
            System.out.println("No guesses yet.");
            return;
        }
        
        System.out.println("\n=== Game History ===");
        for (int i = 0; i < history.size(); i++) {
            History entry = history.get(i);
            System.out.printf("Attempt %d: %s -> %s%n", 
                i + 1, 
                entry.guess().toString(), 
                entry.feedback().toString());
        }
        System.out.println();
    }

    public void displayGameResults(Status status, NumCombination answer, String name) {
        String message = status == Status.WON ? 
            """
            
            ========================================
                      * * * CONGRATULATIONS %s! * * *
                             YOU WON!
            
            The secret combination was: %s
            ========================================
            
            """ :
            """
            
            ========================================
                      - - - GAME OVER %s - - -
                    You ran out of attempts
            
            The secret combination was: %s
            ========================================
            
            """;
        
        System.out.printf(message, name.toUpperCase(), answer.toString());
    }

    public void displayRemainingAttempts(int remaining) {
        if (remaining > 1) {
            System.out.println(remaining + " attempts remaining.");
        } else if (remaining == 1) {
            System.out.println("*** WARNING: LAST ATTEMPT! ***");
        }
    }

    public void displayError(String message) {
        System.out.println("Error: " + message);
    }

    public void displayHint(String numFromAnswer) {
        System.out.println("The combination contains a number " + numFromAnswer);
    }
}
