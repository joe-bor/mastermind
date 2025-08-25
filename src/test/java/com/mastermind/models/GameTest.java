package com.mastermind.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game")
class GameTest {

    private Player testPlayer;
    private NumCombination testAnswer;
    private NumCombination testGuess;

    @BeforeEach
    void setUp() {
        testPlayer = new Player("TestPlayer");
        testAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
        testGuess = new NumCombination(Arrays.asList(0, 1, 2, 3));
    }

    @Nested
    @DisplayName("Game initialization")
    class GameInitialization {

        @Test
        @DisplayName("should initialize game with PENDING status")
        void shouldInitializeGameWithPendingStatus() {
            // Act
            Game game = new Game(testPlayer, testAnswer);

            // Assert
            assertEquals(Status.PENDING, game.getStatus());
            assertEquals(testPlayer, game.getPlayer());
            assertEquals(testAnswer, game.getAnswer());
            assertEquals(10, game.getMaxAttempts());
            assertTrue(game.getGuesses().isEmpty());
            assertTrue(game.getFeedbacks().isEmpty());
        }

        @Test
        @DisplayName("should have 10 remaining attempts initially")
        void shouldHaveTenRemainingAttemptsInitially() {
            // Act
            Game game = new Game(testPlayer, testAnswer);

            // Assert
            assertEquals(10, game.getRemainingAttempts());
        }
    }

    @Nested
    @DisplayName("Game start functionality")
    class GameStartFunctionality {

        @Test
        @DisplayName("should change status from PENDING to IN_PROGRESS when started")
        void shouldChangeStatusFromPendingToInProgressWhenStarted() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);

            // Act
            game.start();

            // Assert
            assertEquals(Status.IN_PROGRESS, game.getStatus());
        }

        @Test
        @DisplayName("should throw exception when starting already started game")
        void shouldThrowExceptionWhenStartingAlreadyStartedGame() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> game.start());
        }

        @Test
        @DisplayName("should throw exception when starting won game")
        void shouldThrowExceptionWhenStartingWonGame() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            game.playerGuess(testAnswer); // Win the game

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> game.start());
        }

        @Test
        @DisplayName("should throw exception when starting lost game")
        void shouldThrowExceptionWhenStartingLostGame() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            
            // Make 10 incorrect guesses
            NumCombination wrongGuess = new NumCombination(Arrays.asList(0, 0, 0, 0));
            for (int i = 0; i < 10; i++) {
                game.playerGuess(wrongGuess);
            }

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> game.start());
        }
    }

    @Nested
    @DisplayName("Player guess functionality")
    class PlayerGuessFunctionality {

        @Test
        @DisplayName("should throw exception when guess is null")
        void shouldThrowExceptionWhenGuessIsNull() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> game.playerGuess(null));
        }

        @Test
        @DisplayName("should throw exception when game not started")
        void shouldThrowExceptionWhenGameNotStarted() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> game.playerGuess(testGuess));
        }

        @Test
        @DisplayName("should throw exception when game already won")
        void shouldThrowExceptionWhenGameAlreadyWon() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            game.playerGuess(testAnswer); // Win the game

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> game.playerGuess(testGuess));
        }

        @Test
        @DisplayName("should throw exception when game already lost")
        void shouldThrowExceptionWhenGameAlreadyLost() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            
            // Make 10 incorrect guesses
            NumCombination wrongGuess = new NumCombination(Arrays.asList(0, 0, 0, 0));
            for (int i = 0; i < 10; i++) {
                game.playerGuess(wrongGuess);
            }

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> game.playerGuess(testGuess));
        }

        @Test
        @DisplayName("should add guess and feedback to history")
        void shouldAddGuessAndFeedbackToHistory() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act
            Feedback feedback = game.playerGuess(testGuess);

            // Assert
            assertEquals(1, game.getGuesses().size());
            assertEquals(1, game.getFeedbacks().size());
            assertEquals(testGuess, game.getGuesses().get(0));
            assertEquals(feedback, game.getFeedbacks().get(0));
        }

        @Test
        @DisplayName("should update remaining attempts after guess")
        void shouldUpdateRemainingAttemptsAfterGuess() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act
            game.playerGuess(testGuess);

            // Assert
            assertEquals(9, game.getRemainingAttempts());
        }
    }

    @Nested
    @DisplayName("Win condition scenarios")
    class WinConditionScenarios {

        @Test
        @DisplayName("should win when guess matches answer exactly")
        void shouldWinWhenGuessMatchesAnswerExactly() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act
            Feedback feedback = game.playerGuess(testAnswer);

            // Assert
            assertEquals(Status.WON, game.getStatus());
            assertEquals(4, feedback.getCorrectPositions());
            assertEquals(4, feedback.getCorrectDigits());
        }

        @Test
        @DisplayName("should win on final attempt when guess is correct")
        void shouldWinOnFinalAttemptWhenGuessIsCorrect() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            
            // Make 9 incorrect guesses
            NumCombination wrongGuess = new NumCombination(Arrays.asList(0, 0, 0, 0));
            for (int i = 0; i < 9; i++) {
                game.playerGuess(wrongGuess);
            }

            // Act - 10th guess is correct
            game.playerGuess(testAnswer);

            // Assert
            assertEquals(Status.WON, game.getStatus());
            assertEquals(10, game.getGuesses().size());
        }
    }

    @Nested
    @DisplayName("Loss condition scenarios")
    class LossConditionScenarios {

        @Test
        @DisplayName("should lose after 10 incorrect guesses")
        void shouldLoseAfterTenIncorrectGuesses() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            NumCombination wrongGuess = new NumCombination(Arrays.asList(0, 0, 0, 0));

            // Act - Make 10 incorrect guesses
            for (int i = 0; i < 10; i++) {
                game.playerGuess(wrongGuess);
            }

            // Assert
            assertEquals(Status.LOST, game.getStatus());
            assertEquals(10, game.getGuesses().size());
            assertEquals(0, game.getRemainingAttempts());
        }

        @Test
        @DisplayName("should continue playing if attempts remain and not won")
        void shouldContinuePlayingIfAttemptsRemainAndNotWon() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();

            // Act
            game.playerGuess(testGuess);

            // Assert
            assertEquals(Status.IN_PROGRESS, game.getStatus());
            assertEquals(9, game.getRemainingAttempts());
        }
    }

    @Nested
    @DisplayName("Game history functionality")
    class GameHistoryFunctionality {

        @Test
        @DisplayName("should return empty history for new game")
        void shouldReturnEmptyHistoryForNewGame() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);

            // Act
            List<History> history = game.getHistory();

            // Assert
            assertTrue(history.isEmpty());
        }

        @Test
        @DisplayName("should return history with guess-feedback pairs")
        void shouldReturnHistoryWithGuessFeedbackPairs() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            NumCombination firstGuess = new NumCombination(Arrays.asList(1, 0, 0, 0));
            NumCombination secondGuess = new NumCombination(Arrays.asList(2, 1, 0, 0));

            // Act
            Feedback firstFeedback = game.playerGuess(firstGuess);
            Feedback secondFeedback = game.playerGuess(secondGuess);
            List<History> history = game.getHistory();

            // Assert
            assertEquals(2, history.size());
            
            History firstHistory = history.get(0);
            assertEquals(firstGuess, firstHistory.guess());
            assertEquals(firstFeedback, firstHistory.feedback());
            
            History secondHistory = history.get(1);
            assertEquals(secondGuess, secondHistory.guess());
            assertEquals(secondFeedback, secondHistory.feedback());
        }

        @Test
        @DisplayName("should maintain history order chronologically")
        void shouldMaintainHistoryOrderChronologically() {
            // Arrange
            Game game = new Game(testPlayer, testAnswer);
            game.start();
            NumCombination[] guesses = {
                new NumCombination(Arrays.asList(1, 0, 0, 0)),
                new NumCombination(Arrays.asList(2, 1, 0, 0)),
                new NumCombination(Arrays.asList(3, 2, 1, 0))
            };

            // Act
            for (NumCombination guess : guesses) {
                game.playerGuess(guess);
            }
            List<History> history = game.getHistory();

            // Assert
            assertEquals(3, history.size());
            for (int i = 0; i < 3; i++) {
                assertEquals(guesses[i], history.get(i).guess());
            }
        }
    }
}