package com.mastermind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Arrange: Capture System.out for output verification
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // Clean up: Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void mainShouldRunWithoutException() {
        // Arrange
        String[] args = {};

        // Act & Assert
        assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void mainShouldDisplayWelcomeMessage() {
        // Arrange
        String[] args = {};
        String expectedWelcomeMessage = "***** Welcome to Mastermind-CLI *****";

        // Act
        Main.main(args);

        // Assert
        String actualOutput = outputStreamCaptor.toString().trim();
        assertTrue(actualOutput.contains(expectedWelcomeMessage),
                "Expected output to contain welcome message: " + expectedWelcomeMessage);
    }

    @Test
    void mainShouldHandleNullArgsGracefully() {
        // Arrange
        String[] args = null;

        // Act & Assert
        assertDoesNotThrow(() -> Main.main(args));
    }
}