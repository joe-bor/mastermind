package com.mastermind.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Feedback")
class FeedbackTest {

    @Nested
    @DisplayName("Factory method validation")
    class FactoryMethodValidation {

        @Test
        @DisplayName("should throw exception when answer is null")
        void shouldThrowExceptionWhenAnswerIsNull() {
            // Arrange
            NumCombination guess = new NumCombination(Arrays.asList(1, 2, 3, 4));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> Feedback.create(null, guess));
        }

        @Test
        @DisplayName("should throw exception when guess is null")
        void shouldThrowExceptionWhenGuessIsNull() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> Feedback.create(answer, null));
        }

        @Test
        @DisplayName("should throw exception when answer and guess have different sizes")
        void shouldThrowExceptionWhenSizeMismatch() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 2, 3), 3, 0, 7);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> Feedback.create(answer, guess));
        }
    }

    @Nested
    @DisplayName("Perfect match scenarios")
    class PerfectMatchScenarios {

        @Test
        @DisplayName("should return perfect feedback when answer equals guess")
        void shouldReturnPerfectFeedbackWhenAnswerEqualsGuess() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 2, 3, 4));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(4, result.getCorrectDigits());
            assertEquals(4, result.getCorrectPositions());
        }
    }

    @Nested
    @DisplayName("No match scenarios")
    class NoMatchScenarios {

        @Test
        @DisplayName("should return zero feedback when no digits match")
        void shouldReturnZeroFeedbackWhenNoDigitsMatch() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(5, 6, 7, 0));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(0, result.getCorrectDigits());
            assertEquals(0, result.getCorrectPositions());
        }
    }

    @Nested
    @DisplayName("Position match scenarios")
    class PositionMatchScenarios {

        @Test
        @DisplayName("should count only position matches correctly")
        void shouldCountOnlyPositionMatchesCorrectly() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 2, 0, 0));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(2, result.getCorrectDigits());
            assertEquals(2, result.getCorrectPositions());
        }
    }

    @Nested
    @DisplayName("Digit match scenarios (wrong positions)")
    class DigitMatchScenarios {

        @Test
        @DisplayName("should count digit matches in wrong positions")
        void shouldCountDigitMatchesInWrongPositions() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(4, 3, 2, 1));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(4, result.getCorrectDigits());
            assertEquals(0, result.getCorrectPositions());
        }

        @Test
        @DisplayName("should count partial digit matches with some positions correct")
        void shouldCountPartialDigitMatchesWithSomePositionsCorrect() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 4, 0, 2));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(3, result.getCorrectDigits()); // 1 (position), 4 (wrong position), 2 (wrong position)
            assertEquals(1, result.getCorrectPositions()); // only 1 at position 0
        }
    }

    @Nested
    @DisplayName("Duplicate handling scenarios")
    class DuplicateHandlingScenarios {

        @Test
        @DisplayName("should handle duplicates correctly when answer has more duplicates")
        void shouldHandleDuplicatesWhenAnswerHasMore() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 1, 1, 2));
            NumCombination guess = new NumCombination(Arrays.asList(1, 1, 3, 4));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(2, result.getCorrectDigits()); // only 2 ones match
            assertEquals(2, result.getCorrectPositions()); // positions 0 and 1
        }

        @Test
        @DisplayName("should handle duplicates correctly when guess has more duplicates")
        void shouldHandleDuplicatesWhenGuessHasMore() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 1, 1, 1));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(1, result.getCorrectDigits()); // only one 1 can match
            assertEquals(1, result.getCorrectPositions()); // position 0
        }

        @Test
        @DisplayName("should handle complex duplicate scenario")
        void shouldHandleComplexDuplicateScenario() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 2, 3));
            NumCombination guess = new NumCombination(Arrays.asList(2, 2, 4, 1));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(3, result.getCorrectDigits()); // 1 position match (2 at pos 1), 2 digit matches (2 at pos 0, 1 at pos 3)
            assertEquals(1, result.getCorrectPositions()); // only position 1 has correct position
        }

        @Test
        @DisplayName("should handle all same digits")
        void shouldHandleAllSameDigits() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(2, 2, 2, 2));
            NumCombination guess = new NumCombination(Arrays.asList(2, 2, 2, 2));

            // Act
            Feedback result = Feedback.create(answer, guess);

            // Assert
            assertEquals(4, result.getCorrectDigits());
            assertEquals(4, result.getCorrectPositions());
        }
    }

    @Nested
    @DisplayName("String representation")
    class StringRepresentation {

        @Test
        @DisplayName("should return 'All incorrect' when no matches")
        void shouldReturnAllIncorrectWhenNoMatches() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(5, 6, 7, 0));
            Feedback feedback = Feedback.create(answer, guess);

            // Act
            String result = feedback.toString();

            // Assert
            assertEquals("All incorrect", result);
        }

        @Test
        @DisplayName("should return 'All correct' when all digits are in correct positions")
        void shouldReturnAllCorrectWhenAllPositionsMatch() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 2, 3, 4));
            Feedback feedback = Feedback.create(answer, guess);

            // Act
            String result = feedback.toString();

            // Assert
            assertEquals("All correct", result);
        }

        @Test
        @DisplayName("should return formatted string for partial matches")
        void shouldReturnFormattedStringForPartialMatches() {
            // Arrange
            NumCombination answer = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination guess = new NumCombination(Arrays.asList(1, 4, 0, 2));
            Feedback feedback = Feedback.create(answer, guess);

            // Act
            String result = feedback.toString();

            // Assert
            assertEquals("3 correct numbers, and 1 correct location", result);
        }
    }
}