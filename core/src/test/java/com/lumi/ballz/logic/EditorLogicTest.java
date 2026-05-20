package com.lumi.ballz.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditorLogicTest {

    private EditorLogic editorLogic;
    private final int WIDTH = 7;
    private final int HEIGHT = 10;

    @BeforeEach
    void setUp() {
        editorLogic = new EditorLogic(WIDTH, HEIGHT);
    }

    @Test
    void testInitialStateIsAllEmpty() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                assertEquals(GridSlot.EMPTY, editorLogic.getSlot(x, y),
                    String.format("Cell [%d][%d] should be EMPTY initially", x, y));
            }
        }
    }

    @Test
    void testCycleSlot_FullSequence() {
        editorLogic.cycleSlot(0, 0);
        assertEquals(GridSlot.ENEMY, editorLogic.getSlot(0, 0));

        editorLogic.cycleSlot(0, 0);
        assertEquals(GridSlot.BONUS, editorLogic.getSlot(0, 0));

        editorLogic.cycleSlot(0, 0);
        assertEquals(GridSlot.HARD_ENEMY, editorLogic.getSlot(0, 0));

        editorLogic.cycleSlot(0, 0);
        assertEquals(GridSlot.EMPTY, editorLogic.getSlot(0, 0));
    }

    @Test
    void testCycleSlot_BoundsCheck() {
        assertDoesNotThrow(() -> {
            editorLogic.cycleSlot(-1, 0);
            editorLogic.cycleSlot(WIDTH, 0);
            editorLogic.cycleSlot(0, -1);
            editorLogic.cycleSlot(0, HEIGHT);
        });
    }

    @Test
    void testClear() {
        editorLogic.cycleSlot(1, 1);
        editorLogic.cycleSlot(2, 2);
        assertNotEquals(GridSlot.EMPTY, editorLogic.getSlot(1, 1));

        editorLogic.clear();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                assertEquals(GridSlot.EMPTY, editorLogic.getSlot(x, y));
            }
        }
    }
}
