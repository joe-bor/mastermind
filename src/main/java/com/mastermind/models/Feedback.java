package com.mastermind.models;

import lombok.Data;

/**
 * Represents the feedback generated after a player's guess.
 * <p>
 * Contains information about:
 * <ul>
 *   <li>The number of correct digits, regardless of position</li>
 *   <li>The number of digits in the correct position</li>
 * </ul>
 */
@Data
public class Feedback {
    private int correctDigits;
    private int correctPositions;

    public String createFeedback() {
        return "Stub";
    }
}
