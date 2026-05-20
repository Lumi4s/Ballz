package com.lumi.ballz.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EntityFactoryTest {

    private TextureAtlas mockAtlas;
    private BitmapFont mockFont;
    private Sprite mockSprite;
    private EntityFactory entityFactory;

    @BeforeEach
    void setUp() {
        mockAtlas = mock(TextureAtlas.class);
        mockFont = mock(BitmapFont.class);
        mockSprite = mock(Sprite.class);

        when(mockAtlas.createSprite(anyString())).thenReturn(mockSprite);

        entityFactory = new EntityFactory(mockAtlas, mockFont);
    }

    @Test
    void testCreateEnemyWithSpecificColor() {
        float x = 100f;
        float y = 200f;
        float size = 50f;
        int hp = 10;
        Color customColor = Color.RED;

        EnemySquare enemy = entityFactory.createEnemy(x, y, size, hp, customColor);

        assertNotNull(enemy, "Enemy cant be null");
        assertEquals(y, enemy.getY(), 0.001f);
        assertEquals(hp, enemy.getHp());
        assertEquals(customColor, enemy.getColor());

        verify(mockAtlas).createSprite("square");
    }

    @Test
    void testCreateEnemyWithRandomColor() {
        EnemySquare enemy = entityFactory.createEnemy(0, 0, 10, 5, null);

        assertNotNull(enemy);
        assertNotNull(enemy.getColor());
        verify(mockAtlas).createSprite("square");
    }

    @Test
    void testCreateBonus() {
        float x = 50f;
        float y = 50f;
        float size = 20f;

        Bonus bonus = entityFactory.createBonus(x, y, size);

        assertNotNull(bonus);
        assertEquals(y, bonus.getY(), 0.001f);

        verify(mockAtlas).createSprite("ui_money");
    }
}
