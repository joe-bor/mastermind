package com.mastermind.services;

import com.mastermind.models.Game;
import com.mastermind.models.NumCombination;
import com.mastermind.models.Player;
import com.mastermind.models.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GameFactory")
class GameFactoryTest {

    @Mock
    private NumberGenerator mockNumberGenerator;
    
    private GameFactory gameFactory;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        gameFactory = new GameFactory(mockNumberGenerator);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockCloseable.close();
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should create GameFactory with NumberGenerator")
        void shouldCreateGameFactoryWithNumberGenerator() {
            // Arrange
            NumberGenerator numberGenerator = mock(NumberGenerator.class);

            // Act & Assert
            assertDoesNotThrow(() -> new GameFactory(numberGenerator));
        }
    }

    @Nested
    @DisplayName("Game creation")
    class GameCreation {

        @Test
        @DisplayName("should create game with provided player and generated answer")
        void shouldCreateGameWithProvidedPlayerAndGeneratedAnswer() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockNumberGenerator.generateNumbers()).thenReturn(expectedAnswer);

            // Act
            Game result = gameFactory.createGame(testPlayer);

            // Assert
            assertNotNull(result);
            assertEquals(testPlayer, result.getPlayer());
            assertEquals(expectedAnswer, result.getAnswer());
            assertEquals(Status.PENDING, result.getStatus());
            assertEquals(10, result.getMaxAttempts());
            assertTrue(result.getGuesses().isEmpty());
            assertTrue(result.getFeedbacks().isEmpty());
        }

        @Test
        @DisplayName("should call number generator exactly once per game creation")
        void shouldCallNumberGeneratorExactlyOncePerGameCreation() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination mockAnswer = new NumCombination(Arrays.asList(0, 1, 2, 3));
            when(mockNumberGenerator.generateNumbers()).thenReturn(mockAnswer);

            // Act
            gameFactory.createGame(testPlayer);

            // Assert
            verify(mockNumberGenerator, times(1)).generateNumbers();
        }

        @Test
        @DisplayName("should create multiple games with different generated answers")
        void shouldCreateMultipleGamesWithDifferentGeneratedAnswers() {
            // Arrange
            Player player1 = new Player("Player1");
            Player player2 = new Player("Player2");
            
            NumCombination answer1 = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination answer2 = new NumCombination(Arrays.asList(5, 6, 7, 0));
            
            when(mockNumberGenerator.generateNumbers())
                .thenReturn(answer1)
                .thenReturn(answer2);

            // Act
            Game game1 = gameFactory.createGame(player1);
            Game game2 = gameFactory.createGame(player2);

            // Assert
            assertEquals(player1, game1.getPlayer());
            assertEquals(answer1, game1.getAnswer());
            
            assertEquals(player2, game2.getPlayer());
            assertEquals(answer2, game2.getAnswer());
            
            verify(mockNumberGenerator, times(2)).generateNumbers();
        }

    }

    @Nested
    @DisplayName("Game initialization")
    class GameInitialization {

        @Test
        @DisplayName("should create games in PENDING state")
        void shouldCreateGamesInPendingState() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination mockAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockNumberGenerator.generateNumbers()).thenReturn(mockAnswer);

            // Act
            Game result = gameFactory.createGame(testPlayer);

            // Assert
            assertEquals(Status.PENDING, result.getStatus());
        }

        @Test
        @DisplayName("should create games with default maximum attempts")
        void shouldCreateGamesWithDefaultMaximumAttempts() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination mockAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockNumberGenerator.generateNumbers()).thenReturn(mockAnswer);

            // Act
            Game result = gameFactory.createGame(testPlayer);

            // Assert
            assertEquals(10, result.getMaxAttempts());
        }

        @Test
        @DisplayName("should create games with empty guess and feedback history")
        void shouldCreateGamesWithEmptyGuessAndFeedbackHistory() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination mockAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockNumberGenerator.generateNumbers()).thenReturn(mockAnswer);

            // Act
            Game result = gameFactory.createGame(testPlayer);

            // Assert
            assertTrue(result.getGuesses().isEmpty());
            assertTrue(result.getFeedbacks().isEmpty());
            assertTrue(result.getHistory().isEmpty());
        }
    }
}