package com.lumi.ballz.logic;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class HighScoreManager {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File storageFile;
    private int highScore = 0;

    public HighScoreManager(File storageFile) {
        this.storageFile = storageFile;
        loadHighScore();
    }

    private void loadHighScore() {
        if (storageFile.exists()) {
            try {
                highScore = mapper.readValue(storageFile, Integer.class);
            } catch (IOException e) {
                System.err.println("Failed to load high score: " + e.getMessage());
                highScore = 0;
            }
        }
    }

    private void saveHighScore() {
        try {
            mapper.writeValue(storageFile, highScore);
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }

    public int getHighScore() {
        return highScore;
    }

    public void checkScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            saveHighScore();
        }
    }
}
