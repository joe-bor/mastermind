package com.mastermind.models;

/**
 * Represents the current status of a Mastermind game.
 * <p>
 * Possible states include:
 * <ul>
 *   <li>{@code IN_PROGRESS} – the game is ongoing</li>
 *   <li>{@code WON} – the player has successfully guessed the combination</li>
 *   <li>{@code LOST} – the player has run out of attempts</li>
 * </ul>
 */
public enum Status {
    IN_PROGRESS,
    WON,
    LOST
}
