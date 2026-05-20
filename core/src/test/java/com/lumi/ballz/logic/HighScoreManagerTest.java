package com.lumi.ballz.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;
    private HighScoreManager highScoreManager;
    private File tempFile;

    @BeforeEach
    void setUp() {
        tempFile = tempDir.resolve("highscore.json").toFile();
        highScoreManager = new HighScoreManager(tempFile);
    }

    @Test
    void testInitialHighScoreIsZero() {
        assertEquals(0, highScoreManager.getHighScore(), "Initial high score should be 0");
    }

    @Test
    void testCheckScoreUpdatesWhenHigher() {
        int newRecord = 100;
        highScoreManager.checkScore(newRecord);

        assertEquals(newRecord, highScoreManager.getHighScore(), "High score should update when a new record is achieved");
    }

    @Test
    void testCheckScoreDoesNotUpdateWhenLower() {
        highScoreManager.checkScore(500);
        assertEquals(500, highScoreManager.getHighScore());

        highScoreManager.checkScore(300);

        assertEquals(500, highScoreManager.getHighScore(), "High score should NOT change if current score is lower");
    }

    @Test
    void testPersistence() {
        int record = 1234;
        highScoreManager.checkScore(record);

        HighScoreManager newManagerInstance = new HighScoreManager(tempFile);

        assertEquals(record, newManagerInstance.getHighScore(), "High score should be loaded from file");
    }
}
