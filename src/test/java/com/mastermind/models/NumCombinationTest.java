package com.mastermind.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NumCombination")
class NumCombinationTest {

    @Test
    @DisplayName("should create valid combination")
    void shouldCreateValidCombination() {
        // Arrange
        List<Integer> numbers = List.of(1, 2, 3, 4);

        // Act & Assert
        assertDoesNotThrow(() -> new NumCombination(numbers));
    }

    @Test
    @DisplayName("should throw exception for null input")
    void shouldThrowExceptionForNullInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new NumCombination(null));
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
