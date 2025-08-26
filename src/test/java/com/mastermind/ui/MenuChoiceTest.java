package com.mastermind.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuChoice")
class MenuChoiceTest {

    @Nested
    @DisplayName("Enum values and properties")
    class EnumValuesAndProperties {

        @Test
        @DisplayName("should have correct value and description for MAKE_GUESS")
        void shouldHaveCorrectValueAndDescriptionForMakeGuess() {
            // Assert
            assertEquals(1, MenuChoice.MAKE_GUESS.getValue());
            assertEquals("Make a guess", MenuChoice.MAKE_GUESS.getDescription());
        }

        @Test
        @DisplayName("should have correct value and description for SHOW_HISTORY")
        void shouldHaveCorrectValueAndDescriptionForShowHistory() {
            // Assert
            assertEquals(2, MenuChoice.SHOW_HISTORY.getValue());
            assertEquals("Show game history", MenuChoice.SHOW_HISTORY.getDescription());
        }

        @Test
        @DisplayName("should have correct value and description for EXIT_GAME")
        void shouldHaveCorrectValueAndDescriptionForExitGame() {
            // Assert
            assertEquals(3, MenuChoice.EXIT_GAME.getValue());
            assertEquals("Exit game", MenuChoice.EXIT_GAME.getDescription());
        }

        @Test
        @DisplayName("should have exactly 3 enum values")
        void shouldHaveExactlyThreeEnumValues() {
            // Assert
            assertEquals(3, MenuChoice.values().length);
        }
    }

    @Nested
    @DisplayName("fromValue method")
    class FromValueMethod {

        @Test
        @DisplayName("should return MAKE_GUESS for value 1")
        void shouldReturnMakeGuessForValueOne() {
            // Act
            MenuChoice result = MenuChoice.fromValue(1);

            // Assert
            assertEquals(MenuChoice.MAKE_GUESS, result);
        }

        @Test
        @DisplayName("should return SHOW_HISTORY for value 2")
        void shouldReturnShowHistoryForValueTwo() {
            // Act
            MenuChoice result = MenuChoice.fromValue(2);

            // Assert
            assertEquals(MenuChoice.SHOW_HISTORY, result);
        }

        @Test
        @DisplayName("should return EXIT_GAME for value 3")
        void shouldReturnExitGameForValueThree() {
            // Act
            MenuChoice result = MenuChoice.fromValue(3);

            // Assert
            assertEquals(MenuChoice.EXIT_GAME, result);
        }

        @Test
        @DisplayName("should return null for invalid positive value")
        void shouldReturnNullForInvalidPositiveValue() {
            // Act & Assert
            assertNull(MenuChoice.fromValue(4));
            assertNull(MenuChoice.fromValue(99));
        }

        @Test
        @DisplayName("should return null for invalid negative value")
        void shouldReturnNullForInvalidNegativeValue() {
            // Act & Assert
            assertNull(MenuChoice.fromValue(-1));
            assertNull(MenuChoice.fromValue(-99));
        }

        @Test
        @DisplayName("should return null for zero")
        void shouldReturnNullForZero() {
            // Act
            MenuChoice result = MenuChoice.fromValue(0);

            // Assert
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("All valid values mapping")
    class AllValidValuesMapping {

        @Test
        @DisplayName("should map all enum values correctly through fromValue")
        void shouldMapAllEnumValuesCorrectlyThroughFromValue() {
            // Test that every enum can be retrieved via its value
            for (MenuChoice choice : MenuChoice.values()) {
                // Act
                MenuChoice retrieved = MenuChoice.fromValue(choice.getValue());

                // Assert
                assertEquals(choice, retrieved, 
                    "fromValue should return " + choice + " for value " + choice.getValue());
            }
        }

        @Test
        @DisplayName("should have unique values for all enum constants")
        void shouldHaveUniqueValuesForAllEnumConstants() {
            // Arrange
            MenuChoice[] choices = MenuChoice.values();

            // Assert - check each pair is unique
            for (int i = 0; i < choices.length; i++) {
                for (int j = i + 1; j < choices.length; j++) {
                    assertNotEquals(choices[i].getValue(), choices[j].getValue(),
                        "Values should be unique: " + choices[i] + " and " + choices[j] + 
                        " both have value " + choices[i].getValue());
                }
            }
        }
    }
}