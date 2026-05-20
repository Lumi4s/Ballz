package com.lumi.ballz.logic;

import com.badlogic.gdx.graphics.Color;

public class EditorLogic {
    public static final Color COLOR_EMPTY = Color.valueOf("2A2A2A");
    public static final Color COLOR_ENEMY = Color.valueOf("FF4B4B");
    public static final Color COLOR_BONUS = Color.valueOf("4BFF8E");
    public static final Color COLOR_HARD = Color.valueOf("FFD44B");
    private final int width;
    private final int height;
    private final GridSlot[][] grid;

    public EditorLogic(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new GridSlot[height][width];
        clear();
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = GridSlot.EMPTY;
            }
        }
    }

    public void cycleSlot(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        GridSlot next;
        switch (grid[y][x]) {
            case EMPTY:
                next = GridSlot.ENEMY;
                break;
            case ENEMY:
                next = GridSlot.BONUS;
                break;
            case BONUS:
                next = GridSlot.HARD_ENEMY;
                break;
            default:
                next = GridSlot.EMPTY;
                break;
        }
        grid[y][x] = next;
    }

    public GridSlot getSlot(int x, int y) {
        return grid[y][x];
    }

    public GridSlot[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
