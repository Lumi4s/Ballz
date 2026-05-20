package com.lumi.ballz.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AIPredictorTest {

    private AIPredictor aiPredictor;
    private Vector2 startPos;
    private int ux = 10;
    private int uy = 10;

    @BeforeEach
    void setUp() {
        aiPredictor = new AIPredictor();
        startPos = new Vector2(5, 5);
    }

    @Test
    void testCalculateBestAngle_WithNoEnemiesAndNoBonuses() {
        Array<EnemySquare> enemies = new Array<>();
        Array<Bonus> bonuses = new Array<>();

        Vector2 resultAngle = aiPredictor.calculateBestAngle(startPos, ux, uy, enemies, bonuses);

        assertNotNull(resultAngle, "Resulting angle vector should not be null");
        float angle = resultAngle.angleDeg();
        assertTrue(angle >= 10 && angle <= 170, "Angle should be within the scanned range [10, 170]. Got: " + angle);
    }

    @Test
    void testCalculateBestAngle_PrefersBonus() {
        Array<EnemySquare> enemies = new Array<>();
        Array<Bonus> bonuses = new Array<>();

        Bonus mockBonus = Mockito.mock(Bonus.class);
        com.badlogic.gdx.math.Rectangle bonusHitbox = new com.badlogic.gdx.math.Rectangle(5, 6, 1, 1);
        when(mockBonus.getHitbox()).thenReturn(bonusHitbox);
        bonuses.add(mockBonus);

        Vector2 resultAngle = aiPredictor.calculateBestAngle(startPos, ux, uy, enemies, bonuses);

        assertNotNull(resultAngle);
        assertTrue(resultAngle.len() > 0);
    }

    @Test
    void testCalculateBestAngle_PrefersKillingEnemy() {
        Array<EnemySquare> enemies = new Array<>();
        Array<Bonus> bonuses = new Array<>();

        Bonus mockBonus = Mockito.mock(Bonus.class);
        when(mockBonus.getHitbox()).thenReturn(new com.badlogic.gdx.math.Rectangle(0, 0, 0.1f, 0.1f));
        bonuses.add(mockBonus);

        EnemySquare mockEnemy = Mockito.mock(EnemySquare.class);
        com.badlogic.gdx.math.Rectangle enemyHitbox = new com.badlogic.gdx.math.Rectangle(5, 6, 1, 1);
        when(mockEnemy.getHitbox()).thenReturn(enemyHitbox);
        Mockito.doReturn(1).when(mockEnemy).getHp();
        enemies.add(mockEnemy);

        Vector2 resultAngle = aiPredictor.calculateBestAngle(startPos, ux, uy, enemies, bonuses);

        assertNotNull(resultAngle);
        float angle = resultAngle.angleDeg();
        assertTrue(angle >= 10 && angle <= 170);
    }
}
