package com.mastermind.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum MenuChoice {
    MAKE_GUESS(1, "Make a guess"),
    SHOW_HISTORY(2, "Show game history"),
    EXIT_GAME(3, "Exit game"),
    GET_HINT(4, "Get a hint");

    private final int value;
    private final String description;

    public static MenuChoice fromValue(int value) {
        return Arrays.stream(values())
            .filter(choice -> choice.value == value)
            .findFirst()
            .orElse(null);
    }
}