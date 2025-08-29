package com.mastermind.controller;

import com.mastermind.models.Difficulty;
import com.mastermind.models.Feedback;
import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;
import com.mastermind.models.Status;
import com.mastermind.services.GameFactory;
import com.mastermind.ui.MenuChoice;
import com.mastermind.ui.UserInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@DisplayName("GameController")
class GameControllerTest {

    @Mock
    private UserInterface mockUI;
    
    @Mock
    private GameFactory mockGameFactory;
    
    @Mock
    private Game mockGame;
    
    private GameController gameController;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        gameController = new GameController(mockUI, mockGameFactory);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockCloseable.close();
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should create GameController with UI and GameFactory")
        void shouldCreateGameControllerWithUiAndGameFactory() {
            // Arrange
            UserInterface ui = mock(UserInterface.class);
            GameFactory gameFactory = mock(GameFactory.class);

            // Act & Assert
            assertDoesNotThrow(() -> new GameController(ui, gameFactory));
        }
    }

    @Nested
    @DisplayName("Game creation and startup")
    class GameCreationAndStartup {

        @Test
        @DisplayName("should display welcome message and create game on startup")
        void shouldDisplayWelcomeMessageAndCreateGameOnStartup() {
            // Arrange
            when(mockUI.promptForPlayerName()).thenReturn("TestPlayer");
            when(mockUI.promptForDifficultyLevel()).thenReturn(2); // NORMAL difficulty
            when(mockGameFactory.createGame(any(Player.class), any())).thenReturn(mockGame);
            when(mockGame.getStatus()).thenReturn(Status.IN_PROGRESS);
            when(mockGame.getPlayer()).thenReturn(new Player("TestPlayer"));
            when(mockUI.displayGameMenu(anyString(), anyInt(), anyInt())).thenReturn(3); // Exit immediately
            when(mockUI.promptForNewGame()).thenReturn(false);

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayWelcomeMessage();
            verify(mockUI, times(1)).promptForPlayerName();
            verify(mockGameFactory, times(1)).createGame(any(Player.class), any());
            verify(mockGame, times(1)).start();
        }

        @Test
        @DisplayName("should handle game creation failure gracefully")
        void shouldHandleGameCreationFailureGracefully() {
            // Arrange
            when(mockUI.promptForPlayerName()).thenReturn("TestPlayer");
            when(mockUI.promptForDifficultyLevel()).thenReturn(2); // NORMAL difficulty
            RuntimeException gameCreationError = new RuntimeException("API failure");
            when(mockGameFactory.createGame(any(Player.class), any())).thenThrow(gameCreationError);
            when(mockUI.promptForNewGame()).thenReturn(false);

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayWelcomeMessage();
            verify(mockUI, times(1)).displayError("Failed to create game: API failure");
            verify(mockUI, times(1)).promptForNewGame();
        }
    }

    @Nested
    @DisplayName("Menu handling")
    class MenuHandling {

        @BeforeEach
        void setUpGameMocks() {
            when(mockUI.promptForPlayerName()).thenReturn("TestPlayer");
            when(mockUI.promptForDifficultyLevel()).thenReturn(2); // NORMAL difficulty
            when(mockGameFactory.createGame(any(Player.class), any())).thenReturn(mockGame);
            when(mockGame.getPlayer()).thenReturn(new Player("TestPlayer"));
            when(mockGame.getDifficulty()).thenReturn(Difficulty.NORMAL);
            when(mockUI.promptForNewGame()).thenReturn(false);
        }

        @Test
        @DisplayName("should handle MAKE_GUESS menu choice")
        void shouldHandleMakeGuessMenuChoice() {
            // Arrange
            NumCombination testGuess = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination testAnswer = new NumCombination(Arrays.asList(5, 6, 7, 0));
            Feedback testFeedback = new Feedback(1, 0, 4);
            
            when(mockGame.getStatus())
                .thenReturn(Status.IN_PROGRESS)  // First check - continue game loop
                .thenReturn(Status.WON);         // After guess - end game
            when(mockGame.getRemainingAttempts()).thenReturn(9);
            when(mockGame.getAnswer()).thenReturn(testAnswer);
            when(mockUI.displayGameMenu(eq("TestPlayer"), eq(9), anyInt())).thenReturn(1); // MAKE_GUESS choice
            when(mockUI.promptForGuess(9, 4, 0, 7)).thenReturn(testGuess);
            when(mockGame.playerGuess(testGuess)).thenReturn(testFeedback);

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayGameMenu(eq("TestPlayer"), eq(9), anyInt());
            verify(mockUI, times(1)).promptForGuess(9, 4, 0, 7);
            verify(mockGame, times(1)).playerGuess(testGuess);
            verify(mockUI, times(1)).displayFeedback(testGuess, testFeedback);
            verify(mockUI, times(1)).displayGameResults(Status.WON, testAnswer, "TestPlayer"); // Called once in handleGuess when game ends
        }

        @Test
        @DisplayName("should handle SHOW_HISTORY menu choice")
        void shouldHandleShowHistoryMenuChoice() {
            // Arrange
            when(mockGame.getStatus())
                .thenReturn(Status.IN_PROGRESS)  // First check - continue game loop
                .thenReturn(Status.LOST);        // Second check - end game
            when(mockGame.getRemainingAttempts()).thenReturn(5);
            when(mockGame.getAnswer()).thenReturn(new NumCombination(Arrays.asList(1, 2, 3, 4)));
            when(mockGame.getHistory()).thenReturn(Collections.emptyList());
            when(mockUI.displayGameMenu(eq("TestPlayer"), eq(5), anyInt())).thenReturn(2); // SHOW_HISTORY choice

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayGameMenu(eq("TestPlayer"), eq(5), anyInt());
            verify(mockUI, times(1)).displayGameHistory(Collections.emptyList());
        }

        @Test
        @DisplayName("should handle EXIT_GAME menu choice")
        void shouldHandleExitGameMenuChoice() {
            // Arrange
            when(mockGame.getStatus()).thenReturn(Status.IN_PROGRESS);
            when(mockGame.getRemainingAttempts()).thenReturn(7);
            when(mockUI.displayGameMenu(eq("TestPlayer"), eq(7), anyInt())).thenReturn(3); // EXIT_GAME choice

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayGameMenu(eq("TestPlayer"), eq(7), anyInt());
            // Should not call displayGameResults when user exits manually
            verify(mockUI, never()).displayGameResults(any(Status.class), any(NumCombination.class), anyString());
        }

        @Test
        @DisplayName("should handle invalid menu choice")
        void shouldHandleInvalidMenuChoice() {
            // Arrange
            when(mockGame.getStatus())
                .thenReturn(Status.IN_PROGRESS)  // First check - continue game loop
                .thenReturn(Status.IN_PROGRESS)  // Second check after invalid choice - continue
                .thenReturn(Status.IN_PROGRESS); // Third check - allow exit choice
            when(mockGame.getRemainingAttempts()).thenReturn(8);
            when(mockUI.displayGameMenu(eq("TestPlayer"), eq(8), anyInt()))
                .thenReturn(99)  // Invalid choice first
                .thenReturn(3);  // Then exit

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(2)).displayGameMenu(eq("TestPlayer"), eq(8), anyInt());
            verify(mockUI, times(1)).displayError("Invalid menu choice. Please try again.");
        }

        @Test
        @DisplayName("should handle GET_HINT menu choice")
        void shouldHandleGetHintMenuChoice() {
            // Arrange
            when(mockGame.getStatus())
                .thenReturn(Status.IN_PROGRESS)  // First check - continue game loop
                .thenReturn(Status.LOST);        // Second check - end game
            when(mockGame.getRemainingAttempts()).thenReturn(6);
            when(mockGame.getAnswer()).thenReturn(new NumCombination(Arrays.asList(1, 2, 3, 4)));
            when(mockGame.getHint()).thenReturn("3");
            when(mockUI.displayGameMenu(eq("TestPlayer"), eq(6), anyInt())).thenReturn(4); // GET_HINT choice

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayGameMenu(eq("TestPlayer"), eq(6), anyInt());
            verify(mockGame, times(1)).getHint();
            verify(mockUI, times(1)).displayHint("3");
        }
    }

    @Nested
    @DisplayName("Play again functionality")
    class PlayAgainFunctionality {

        @Test
        @DisplayName("should support playing multiple games")
        void shouldSupportPlayingMultipleGames() {
            // Arrange
            Game firstGame = mock(Game.class);
            Game secondGame = mock(Game.class);
            
            when(mockUI.promptForPlayerName())
                .thenReturn("Player1")
                .thenReturn("Player2");
            
            when(mockUI.promptForDifficultyLevel()).thenReturn(2); // NORMAL difficulty for both games
            
            when(firstGame.getPlayer()).thenReturn(new Player("Player1"));
            when(secondGame.getPlayer()).thenReturn(new Player("Player2"));
            
            when(mockGameFactory.createGame(any(Player.class), any()))
                .thenReturn(firstGame)
                .thenReturn(secondGame);
            
            when(firstGame.getStatus()).thenReturn(Status.WON);
            when(firstGame.getAnswer()).thenReturn(new NumCombination(Arrays.asList(1, 2, 3, 4)));
            
            when(secondGame.getStatus()).thenReturn(Status.LOST);
            when(secondGame.getAnswer()).thenReturn(new NumCombination(Arrays.asList(5, 6, 7, 0)));
            
            when(mockUI.promptForNewGame())
                .thenReturn(true)   // Play again after first game
                .thenReturn(false); // Stop after second game

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).displayWelcomeMessage(); // Only once at startup
            verify(mockGameFactory, times(2)).createGame(any(Player.class), any()); // Two games created
            verify(firstGame, times(1)).start();
            verify(secondGame, times(1)).start();
            verify(mockUI, times(2)).promptForNewGame(); // Asked twice
        }

        @Test
        @DisplayName("should stop when user declines to play again")
        void shouldStopWhenUserDeclinesToPlayAgain() {
            // Arrange
            when(mockUI.promptForPlayerName()).thenReturn("TestPlayer");
            when(mockUI.promptForDifficultyLevel()).thenReturn(2); // NORMAL difficulty
            when(mockGameFactory.createGame(any(Player.class), any())).thenReturn(mockGame);
            when(mockGame.getStatus()).thenReturn(Status.WON);
            when(mockGame.getAnswer()).thenReturn(new NumCombination(Arrays.asList(1, 2, 3, 4)));
            when(mockGame.getPlayer()).thenReturn(new Player("TestPlayer"));
            when(mockUI.promptForNewGame()).thenReturn(false); // Don't play again

            // Act
            gameController.startGame();

            // Assert
            verify(mockUI, times(1)).promptForNewGame();
            verify(mockGameFactory, times(1)).createGame(any(Player.class), any()); // Only one game
        }
    }
}