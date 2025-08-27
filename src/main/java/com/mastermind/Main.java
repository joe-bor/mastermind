package com.mastermind;

import com.mastermind.controller.GameController;
import com.mastermind.services.GameFactory;
import com.mastermind.services.RandomNumberApiClient;
import com.mastermind.services.RandomNumberGenerator;
import com.mastermind.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        new GameController(new UserInterface(),
                new GameFactory(new RandomNumberGenerator(new RandomNumberApiClient())))
                .startGame();
    }
}