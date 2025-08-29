package com.mastermind.services;

import com.mastermind.models.NumCombination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RandomNumberGenerator")
class RandomNumberGeneratorTest {

    @Mock
    private RandomNumberApiClient mockApiClient;
    
    private RandomNumberGenerator generator;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        generator = new RandomNumberGenerator(mockApiClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockCloseable.close();
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should create generator with RandomNumberApiClient")
        void shouldCreateGeneratorWithRandomNumberApiClient() {
            // Arrange
            RandomNumberApiClient apiClient = mock(RandomNumberApiClient.class);

            // Act & Assert
            assertDoesNotThrow(() -> new RandomNumberGenerator(apiClient));
        }
    }

    @Nested
    @DisplayName("Successful API responses")
    class SuccessfulApiResponses {

        @Test
        @DisplayName("should return API result on first attempt success")
        void shouldReturnApiResultOnFirstAttemptSuccess() {
            // Arrange
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            when(mockApiClient.getRandomNums(anyInt(), anyInt())).thenReturn(expectedAnswer);

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertEquals(expectedAnswer, result);
            verify(mockApiClient, times(1)).getRandomNums(anyInt(), anyInt());
        }

        @Test
        @DisplayName("should return API result with different number combinations")
        void shouldReturnApiResultWithDifferentNumberCombinations() {
            // Arrange
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(7, 0, 5, 2));
            when(mockApiClient.getRandomNums(anyInt(), anyInt())).thenReturn(expectedAnswer);

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertEquals(expectedAnswer, result);
            assertEquals(Arrays.asList(7, 0, 5, 2), result.getNumbers());
        }
    }

    @Nested
    @DisplayName("Retry behavior")
    class RetryBehavior {

        @Test
        @DisplayName("should succeed on first retry attempt")
        void shouldSucceedOnFirstRetryAttempt() {
            // Arrange
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(3, 4, 5, 6));
            when(mockApiClient.getRandomNums(anyInt(), anyInt()))
                    .thenThrow(new RandomNumberApiException("First attempt fails"))
                    .thenReturn(expectedAnswer); // Second attempt succeeds

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertEquals(expectedAnswer, result);
            verify(mockApiClient, times(2)).getRandomNums(anyInt(), anyInt());
        }

        @Test
        @DisplayName("should succeed on second retry attempt")
        void shouldSucceedOnSecondRetryAttempt() {
            // Arrange
            NumCombination expectedAnswer = new NumCombination(Arrays.asList(0, 1, 7, 6));
            when(mockApiClient.getRandomNums(anyInt(), anyInt()))
                    .thenThrow(new RandomNumberApiException("First attempt fails"))
                    .thenThrow(new RandomNumberApiException("Second attempt fails"))
                    .thenReturn(expectedAnswer); // Third attempt succeeds

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertEquals(expectedAnswer, result);
            verify(mockApiClient, times(3)).getRandomNums(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Fallback behavior")
    class FallbackBehavior {

        @Test
        @DisplayName("should fall back to local generation when all API attempts fail")
        void shouldFallBackToLocalGenerationWhenAllApiAttemptsFail() {
            // Arrange
            when(mockApiClient.getRandomNums(anyInt(), anyInt())).thenThrow(new RandomNumberApiException("API always fails"));

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertNotNull(result.getNumbers());
            assertEquals(4, result.getNumbers().size());
            
            // Validate fallback numbers are within valid range
            List<Integer> numbers = result.getNumbers();
            for (Integer number : numbers) {
                assertTrue(number >= 0 && number <= 7, "Number " + number + " should be in range 0-7");
            }
            
            // Verify all retry attempts were made
            verify(mockApiClient, times(3)).getRandomNums(anyInt(), anyInt());
        }

        @Test
        @DisplayName("should generate different fallback numbers on multiple calls")
        void shouldGenerateDifferentFallbackNumbersOnMultipleCalls() {
            // Arrange
            when(mockApiClient.getRandomNums(anyInt(), anyInt())).thenThrow(new RandomNumberApiException("API always fails"));

            // Act - Generate multiple sets
            NumCombination result1 = generator.generateNumbers();
            NumCombination result2 = generator.generateNumbers();
            NumCombination result3 = generator.generateNumbers();

            // Assert - All should be valid
            assertNotNull(result1);
            assertNotNull(result2);
            assertNotNull(result3);
            
            // All should have correct size
            assertEquals(4, result1.getNumbers().size());
            assertEquals(4, result2.getNumbers().size());
            assertEquals(4, result3.getNumbers().size());
            
            // At least one should be different (extremely unlikely to be all the same)
            boolean allSame = result1.equals(result2) && result2.equals(result3);
            assertFalse(allSame, "Generated numbers should have some variation");
            
            // Verify API was called 3 times for each generation (3 generations × 3 attempts each)
            verify(mockApiClient, times(9)).getRandomNums(anyInt(), anyInt());
        }

        @Test
        @DisplayName("should handle thread interruption during retry")
        void shouldHandleThreadInterruptionDuringRetry() {
            // Arrange
            when(mockApiClient.getRandomNums(anyInt(), anyInt())).thenThrow(new RandomNumberApiException("API fails"));
            
            // Interrupt the current thread to simulate interruption during sleep
            Thread.currentThread().interrupt();

            // Act
            NumCombination result = generator.generateNumbers();

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getNumbers().size());
            
            // Validate fallback numbers are within valid range
            List<Integer> numbers = result.getNumbers();
            for (Integer number : numbers) {
                assertTrue(number >= 0 && number <= 7, "Number " + number + " should be in range 0-7");
            }
            
            // Should have tried at least once before interruption caused fallback
            verify(mockApiClient, atLeast(1)).getRandomNums(anyInt(), anyInt());
            
            // Clean up the interrupted status
            assertTrue(Thread.interrupted(), "Thread should have been interrupted");
        }
    }
}