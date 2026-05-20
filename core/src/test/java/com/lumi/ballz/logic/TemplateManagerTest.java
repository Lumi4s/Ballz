package com.lumi.ballz.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TemplateManagerTest {

    private TemplateManager templateManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        templateManager = new TemplateManager(tempDir.toString() + "/");
    }

    @Test
    void testSaveAndLoadTemplate() {
        GridSlot[][] originalGrid = {
            {GridSlot.EMPTY, GridSlot.ENEMY, GridSlot.BONUS},
            {GridSlot.HARD_ENEMY, GridSlot.EMPTY, GridSlot.ENEMY},
            {GridSlot.BONUS, GridSlot.HARD_ENEMY, GridSlot.EMPTY}
        };

        String templateName = "test_level_pattern";
        templateManager.saveTemplate(originalGrid, templateName);
        GridSlot[][] loadedGrid = templateManager.loadTemplate(templateName);

        assertNotNull(loadedGrid, "Loaded grid should not be null");
        assertEquals(originalGrid.length, loadedGrid.length, "Rows count mismatch");
        assertEquals(originalGrid[0].length, loadedGrid[0].length, "Columns count mismatch");

        for (int i = 0; i < originalGrid.length; i++) {
            for (int j = 0; j < originalGrid[i].length; j++) {
                assertEquals(originalGrid[i][j], loadedGrid[i][j],
                    String.format("Mismatch at [%d][%d]: expected %s, got %s", i, j, originalGrid[i][j], loadedGrid[i][j]));
            }
        }
    }

    @Test
    void testLoadNonExistentTemplate() {
        GridSlot[][] result = templateManager.loadTemplate("does_not_exist");
        assertNull(result, "Loading a non-existent file should return null");
    }

    @Test
    void testSaveAndLoadEmptyGrid() {
        GridSlot[][] emptyGrid = new GridSlot[0][0];
        String templateName = "empty_grid";

        templateManager.saveTemplate(emptyGrid, templateName);
        GridSlot[][] loadedGrid = templateManager.loadTemplate(templateName);

        assertNotNull(loadedGrid);
        assertEquals(0, loadedGrid.length, "Loaded grid should be empty");
    }
}
