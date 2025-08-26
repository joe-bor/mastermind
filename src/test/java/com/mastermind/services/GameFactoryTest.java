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
    private RandomNumberApiClient mockApiClient;
    
    private GameFactory gameFactory;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        gameFactory = new GameFactory(mockApiClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockCloseable.close();
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should create GameFactory with RandomNumberApiClient")
        void shouldCreateGameFactoryWithRandomNumberApiClient() {
            // Arrange
            RandomNumberApiClient apiClient = mock(RandomNumberApiClient.class);

            // Act & Assert
            assertDoesNotThrow(() -> new GameFactory(apiClient));
        }
    }

    @Nested
    @DisplayName("Game creation")
    class GameCreation {

        @Test
        @DisplayName("should create game with provided player and API-generated answer")
        void shouldCreateGameWithProvidedPlayerAndApiGeneratedAnswer() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockApiClient.getRandomNums()).thenReturn(expectedAnswer);

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
        @DisplayName("should call API client exactly once per game creation")
        void shouldCallApiClientExactlyOncePerGameCreation() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            NumCombination mockAnswer = new NumCombination(Arrays.asList(0, 1, 2, 3));
            when(mockApiClient.getRandomNums()).thenReturn(mockAnswer);

            // Act
            gameFactory.createGame(testPlayer);

            // Assert
            verify(mockApiClient, times(1)).getRandomNums();
        }

        @Test
        @DisplayName("should create multiple games with different API responses")
        void shouldCreateMultipleGamesWithDifferentApiResponses() {
            // Arrange
            Player player1 = new Player("Player1");
            Player player2 = new Player("Player2");
            
            NumCombination answer1 = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination answer2 = new NumCombination(Arrays.asList(5, 6, 7, 0));
            
            when(mockApiClient.getRandomNums())
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
            
            verify(mockApiClient, times(2)).getRandomNums();
        }

        @Test
        @DisplayName("should propagate API client exceptions")
        void shouldPropagateApiClientExceptions() {
            // Arrange
            Player testPlayer = new Player("TestPlayer");
            RuntimeException apiException = new RuntimeException("API failure");
            when(mockApiClient.getRandomNums()).thenThrow(apiException);

            // Act & Assert
            RuntimeException thrownException = assertThrows(RuntimeException.class, 
                () -> gameFactory.createGame(testPlayer));
            
            assertEquals("API failure", thrownException.getMessage());
            verify(mockApiClient, times(1)).getRandomNums();
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
            when(mockApiClient.getRandomNums()).thenReturn(mockAnswer);

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
            when(mockApiClient.getRandomNums()).thenReturn(mockAnswer);

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
            when(mockApiClient.getRandomNums()).thenReturn(mockAnswer);

            // Act
            Game result = gameFactory.createGame(testPlayer);

            // Assert
            assertTrue(result.getGuesses().isEmpty());
            assertTrue(result.getFeedbacks().isEmpty());
            assertTrue(result.getHistory().isEmpty());
        }
    }
}