package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BonusTest {

    private Bonus bonus;
    private Sprite mockSprite;
    private float startX = 5f;
    private float startY = 5f;
    private float size = 1f;

    @BeforeEach
    void setUp() {
        mockSprite = mock(Sprite.class);
        bonus = new Bonus(mockSprite, startX, startY, size);
    }

    @Test
    void testInitialState() {
        assertFalse(bonus.isCollected(), "Bonus should not be collected initially");
        assertEquals(startY, bonus.getY(), 0.001f, "Initial Y should match startY");
    }

    @Test
    void testCollect() {
        bonus.collect();
        assertTrue(bonus.isCollected(), "Bonus should be marked as collected");
    }

    @Test
    void testMoveDownAndSmoothUpdate() {
        bonus.moveDown();

        float delta = 0.1f;
        bonus.update(delta);

        assertTrue(bonus.getY() < startY, "Y should decrease after moveDown and update");
        assertTrue(bonus.getY() > 4.0f, "Y should not jump directly to target_y due to lerp");

        for (int i = 0; i < 100; i++) {
            bonus.update(0.1f);
        }
        assertEquals(4.0f, bonus.getY(), 0.001f, "Bonus should eventually reach target_y");
    }
}
