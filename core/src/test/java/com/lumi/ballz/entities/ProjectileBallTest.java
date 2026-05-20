package com.lumi.ballz.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lumi.ballz.logic.BallState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectileBallTest {

    private ProjectileBall ball;
    private Vector2 startPos;
    private Vector2 direction;
    private float speed = 10f;
    private int ux = 10;
    private int uy = 10;

    @BeforeEach
    void setUp() {
        startPos = new Vector2(5, 5);
        direction = new Vector2(1, 0);
        ball = new ProjectileBall(startPos, direction, speed);
    }

    @Test
    void testUpdate_MovesPositionCorrectly() {
        float delta = 0.1f;
        ball.update(delta, ux, uy);

        assertEquals(6.0f, ball.getPosition().x, 0.001f);
        assertEquals(5.0f, ball.getPosition().y, 0.001f);
    }

    @Test
    void testUpdate_BouncesOffRightWall() {
        ProjectileBall wallBall = new ProjectileBall(new Vector2(9.8f, 5f), new Vector2(1, 0), 10f);

        wallBall.update(0.1f, ux, uy);

        assertTrue(wallBall.getVelocity().x < 0, "Velocity X should be negative after hitting right wall");
        assertTrue(wallBall.getPosition().x <= ux - wallBall.radius);
    }

    @Test
    void testMoveTo_ChangesStatusToWaitingWhenReached() {
        Vector2 target = new Vector2(6, 5);
        float delta = 1.0f;

        ball.moveTo(delta, target, speed);

        assertEquals(target.x, ball.getPosition().x, 0.001f);
        assertEquals(target.y, ball.getPosition().y, 0.001f);
        assertEquals(BallState.WAITING, ball.getStatus(), "Status should be WAITING after reaching target");
    }

    @Test
    void testCheckCollision_CallsEnemyHurtAndReflects() {
        EnemySquare mockEnemy = Mockito.mock(EnemySquare.class);
        Rectangle enemyHitbox = new Rectangle(6, 4.8f, 1, 1);
        when(mockEnemy.getHitbox()).thenReturn(enemyHitbox);

        ball.update(0.1f, ux, uy);
        ball.checkCollision(mockEnemy, 1);
        verify(mockEnemy, times(1)).hurt(1);

        assertTrue(ball.getVelocity().x < 0, "Ball should reflect after collision");
    }

    @Test
    void testUpdate_BouncesOffLeftWall() {
        ProjectileBall wallBall = new ProjectileBall(new Vector2(0.2f, 5f), new Vector2(-1, 0), 10f);

        wallBall.update(0.1f, ux, uy);

        assertTrue(wallBall.getVelocity().x > 0, "Velocity X should be positive after hitting left wall");
        assertTrue(wallBall.getPosition().x >= wallBall.radius);
    }

    @Test
    void testUpdate_BouncesOffCeiling() {
        ProjectileBall ceilingBall = new ProjectileBall(new Vector2(5f, 9.8f), new Vector2(0, 1), 10f);

        ceilingBall.update(0.1f, ux, uy);

        assertTrue(ceilingBall.getVelocity().y < 0, "Velocity Y should be negative after hitting ceiling");
        assertTrue(ceilingBall.getPosition().y <= uy);
    }

    @Test
    void testMoveTo_DoesNotReachTarget_StatusRemainsUnchanged() {
        Vector2 target = new Vector2(5f, 50f);
        float delta = 0.016f;

        ball.moveTo(delta, target, speed);

        assertEquals(BallState.FIRE, ball.getStatus(), "Status should remain FIRE when target not reached");
        assertNotEquals(target.y, ball.getPosition().y, 0.1f);
    }

    @Test
    void testCheckCollision_NoHit_HurtNotCalled() {
        EnemySquare mockEnemy = Mockito.mock(EnemySquare.class);
        Rectangle enemyHitbox = new Rectangle(20, 20, 1, 1);
        when(mockEnemy.getHitbox()).thenReturn(enemyHitbox);

        ball.checkCollision(mockEnemy, 1);

        verify(mockEnemy, never()).hurt(anyInt());
    }
}
