package com.mastermind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final InputStream originalIn = System.in;

    @AfterEach
    void tearDown() {
        // Clean up: Restore original System.in
        System.setIn(originalIn);
    }

    @Test
    void mainShouldRunWithoutException() {
        // Arrange - Provide input to exit game immediately
        System.setIn(new ByteArrayInputStream("3\nn\n".getBytes())); // Exit game, no to play again

        // Act & Assert - Should not throw any exceptions during dependency wiring and startup
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}