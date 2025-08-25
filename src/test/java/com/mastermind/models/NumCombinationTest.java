package com.mastermind.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NumCombination")
class NumCombinationTest {

    @Nested
    @DisplayName("Default constructor (4 numbers, 0-7 range)")
    class DefaultConstructor {

        @Test
        @DisplayName("should create valid combination with default constraints")
        void shouldCreateValidCombinationWithDefaults() {
            // Arrange
            List<Integer> validNumbers = Arrays.asList(0, 1, 3, 7);

            // Act & Assert
            assertDoesNotThrow(() -> new NumCombination(validNumbers));
        }

        @Test
        @DisplayName("should throw exception when not exactly 4 numbers")
        void shouldThrowExceptionWhenNotExactlyFourNumbers() {
            // Arrange
            List<Integer> tooFewNumbers = Arrays.asList(1, 2, 3);
            List<Integer> tooManyNumbers = Arrays.asList(1, 2, 3, 4, 5);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> new NumCombination(tooFewNumbers));
            assertThrows(IllegalArgumentException.class, () -> new NumCombination(tooManyNumbers));
        }

        @Test
        @DisplayName("should throw exception when numbers outside 0-7 range")
        void shouldThrowExceptionWhenNumbersOutsideDefaultRange() {
            // Arrange
            List<Integer> belowRange = Arrays.asList(-1, 1, 2, 3);
            List<Integer> aboveRange = Arrays.asList(1, 2, 3, 8);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> new NumCombination(belowRange));
            assertThrows(IllegalArgumentException.class, () -> new NumCombination(aboveRange));
        }

        @Test
        @DisplayName("should allow duplicate numbers")
        void shouldAllowDuplicateNumbers() {
            // Arrange
            List<Integer> duplicates = Arrays.asList(1, 1, 2, 2);

            // Act & Assert
            assertDoesNotThrow(() -> new NumCombination(duplicates));
        }
    }

    @Nested
    @DisplayName("Configurable constructor")
    class ConfigurableConstructor {

        @Test
        @DisplayName("should create combination with custom size and range")
        void shouldCreateCombinationWithCustomSizeAndRange() {
            // Arrange
            List<Integer> numbers = Arrays.asList(1, 5, 9);

            // Act & Assert
            assertDoesNotThrow(() -> new NumCombination(numbers, 3, 1, 10));
        }

        @Test
        @DisplayName("should validate against custom constraints")
        void shouldValidateAgainstCustomConstraints() {
            // Arrange
            List<Integer> validNumbers = Arrays.asList(5, 6);
            List<Integer> invalidSize = Arrays.asList(5, 6, 7);
            List<Integer> invalidRange = Arrays.asList(4, 6); // 4 is below min of 5

            // Act & Assert
            assertDoesNotThrow(() -> new NumCombination(validNumbers, 2, 5, 10));
            assertThrows(IllegalArgumentException.class,
                    () -> new NumCombination(invalidSize, 2, 5, 10));
            assertThrows(IllegalArgumentException.class,
                    () -> new NumCombination(invalidRange, 2, 5, 10));
        }
    }

    @Nested
    @DisplayName("Parse method")
    class ParseMethod {

        @Test
        @DisplayName("should parse valid space-separated string")
        void shouldParseValidSpaceSeparatedString() {
            // Arrange
            String input = "1 2 3 4";

            // Act
            NumCombination result = NumCombination.parse(input);

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(1, 2, 3, 4), result.getNumbers());
        }

        @Test
        @DisplayName("should handle extra whitespace")
        void shouldHandleExtraWhitespace() {
            // Arrange
            String input = " 0  1   3    7 ";

            // Act & Assert
            assertDoesNotThrow(() -> NumCombination.parse(input));
        }

        @Test
        @DisplayName("should throw exception for invalid input")
        void shouldThrowExceptionForInvalidInput() {
            // Arrange
            String nonNumeric = "1 a 3 4";
            String empty = "";
            String nullInput = null;

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> NumCombination.parse(nonNumeric));
            assertThrows(IllegalArgumentException.class, () -> NumCombination.parse(empty));
            assertThrows(IllegalArgumentException.class, () -> NumCombination.parse(nullInput));
        }
    }

    @Nested
    @DisplayName("Equality and behavior")
    class EqualityAndBehavior {

        @Test
        @DisplayName("should be equal when same numbers in same order")
        void shouldBeEqualWhenSameNumbers() {
            // Arrange
            NumCombination combo1 = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination combo2 = new NumCombination(Arrays.asList(1, 2, 3, 4));

            // Act & Assert
            assertEquals(combo1, combo2);
            assertEquals(combo1.hashCode(), combo2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when different order or numbers")
        void shouldNotBeEqualWhenDifferent() {
            // Arrange
            NumCombination original = new NumCombination(Arrays.asList(1, 2, 3, 4));
            NumCombination differentOrder = new NumCombination(Arrays.asList(4, 3, 2, 1));
            NumCombination differentNumbers = new NumCombination(Arrays.asList(1, 2, 3, 5));

            // Act & Assert
            assertNotEquals(original, differentOrder);
            assertNotEquals(original, differentNumbers);
        }

        @Test
        @DisplayName("should return immutable list")
        void shouldReturnImmutableList() {
            // Arrange
            NumCombination combo = new NumCombination(Arrays.asList(1, 2, 3, 4));

            // Act
            List<Integer> numbers = combo.getNumbers();

            // Assert
            assertEquals(Arrays.asList(1, 2, 3, 4), numbers);
            assertThrows(UnsupportedOperationException.class, () -> numbers.add(5));
        }
    }
}