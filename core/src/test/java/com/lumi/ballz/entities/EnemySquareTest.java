package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnemySquareTest {

    private EnemySquare enemy;
    private Sprite mockSprite;
    private BitmapFont mockFont;
    private float startX = 5f;
    private float startY = 5f;
    private float size = 1f;
    private int initialHp = 3;

    @BeforeEach
    void setUp() {
        mockSprite = mock(Sprite.class);
        mockFont = mock(BitmapFont.class);
        enemy = new EnemySquare(mockSprite, startX, startY, size, initialHp, Color.RED, mockFont);
    }

    @Test
    void testHurtAndIsDead() {
        assertEquals(initialHp, enemy.getHp());
        assertFalse(enemy.isDead());

        enemy.hurt(1);
        assertEquals(2, enemy.getHp());
        assertFalse(enemy.isDead());

        enemy.hurt(2);
        assertEquals(0, enemy.getHp());
        assertTrue(enemy.isDead(), "Enemy should be dead when HP <= 0");
    }

    @Test
    void testMoveDownAndSmoothUpdate() {
        float initialY = enemy.getY();
        enemy.moveDown();

        enemy.update(0.1f);

        assertTrue(enemy.getY() < initialY, "Enemy should be moving down");
        assertTrue(enemy.getY() > 4.0f, "Enemy should not jump directly to target_y due to lerp");

        for (int i = 0; i < 100; i++) {
            enemy.update(0.1f);
        }
        assertEquals(4.0f, enemy.getY(), 0.001f, "Enemy should eventually reach target_y");
    }
}
