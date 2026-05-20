package com.lumi.ballz.logic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameControllerTest {
    private GameController controller;
    private GameController.GameEventListener listener;
    private StubEntityFactory factory;
    private TemplateManager templateManager;

    @BeforeEach
    void setUp() {
        listener = mock(GameController.GameEventListener.class);
        factory = new StubEntityFactory();
        templateManager = mock(TemplateManager.class);
        when(templateManager.loadTemplate(anyString())).thenReturn(null);

        controller = new GameController(7, 12, factory, templateManager, listener);
    }

    @Test
    void initialState_IsPlaying() {
        assertEquals(GameController.State.PLAYING, controller.getState());
    }

    @Test
    void initialState_NoBallsInFlight() {
        assertEquals(0, controller.getBallz().size);
    }

    @Test
    void initialState_StartPosAtDefaultX() {
        assertEquals(3.5f, controller.getStartPos().x, 0.001f);
        assertEquals(GameController.FLOOR_Y, controller.getStartPos().y, 0.001f);
    }

    @Test
    void initialState_ScoreIsZero() {
        assertEquals(0, controller.getScore());
    }

    @Test
    void shoot_WithValidDirection_StartsTurn() {
        controller.shoot(new Vector2(0, 1));

        assertTrue(controller.isTurnProcessing());
    }

    @Test
    void shoot_WhileTurnProcessing_IsIgnored() {
        controller.shoot(new Vector2(0, 1));
        controller.shoot(new Vector2(1, 0));

        assertEquals(0f, controller.getAimDir().x, 0.001f);
        assertEquals(1f, controller.getAimDir().y, 0.001f);
    }

    @Test
    void shoot_NormalizesDirection() {
        controller.shoot(new Vector2(3, 4));
        float len = controller.getAimDir().len();
        assertEquals(1f, len, 0.001f);
    }

    @Test
    void shoot_WhenGameOver_IsIgnored() {
        triggerGameOver();

        controller.shoot(new Vector2(0, 1));

        assertFalse(controller.isTurnProcessing());
    }

    @Test
    void update_SpawnsBallAfterInterval() {
        controller.shoot(new Vector2(0, 1));
        controller.update(0.15f);

        assertEquals(1, controller.getBallz().size);
    }

    @Test
    void update_DoesNothingWhenGameOver() {
        triggerGameOver();
        int enemiesBefore = controller.getEnemies().size;

        controller.shoot(new Vector2(0, 1));
        controller.update(1f);

        assertEquals(0, controller.getBallz().size);
        assertEquals(enemiesBefore, controller.getEnemies().size);
    }

    @Test
    void score_IncreasesBy10WhenEnemyDies() {
        StubEntityFactory killingFactory = new StubEntityFactory() {
            int call = 0;

            public EnemySquare createEnemy(float x, float y, float size,
                                           int hp, com.badlogic.gdx.graphics.Color color) {
                EnemySquare e = mock(EnemySquare.class);
                when(e.isDead()).thenReturn(call++ > 0);
                when(e.getY()).thenReturn(y);
                when(e.getHitbox()).thenReturn(new Rectangle(x, y, size, size));
                return e;
            }

            public Bonus createBonus(float x, float y, float size) {
                return factory.createBonus(x, y, size);
            }
        };

        GameController ctrl = new GameController(7, 12, killingFactory, templateManager, listener);

        ctrl.update(0.016f);
        ctrl.update(0.016f);

        assertTrue(ctrl.getScore() >= 10);
    }

    @Test
    void onGameOver_CalledWhenEnemyReachesFloor() {
        triggerGameOver();
        verify(listener, atLeastOnce()).onGameOver(anyInt());
    }

    @Test
    void onGameOver_StateChangesToGameOver() {
        triggerGameOver();
        assertEquals(GameController.State.GAME_OVER, controller.getState());
    }

    @Test
    void restart_ResetsScoreToZero() {
        triggerGameOver();
        controller.restart();
        assertEquals(0, controller.getScore());
    }

    @Test
    void restart_ResetsStateToPlaying() {
        triggerGameOver();
        controller.restart();
        assertEquals(GameController.State.PLAYING, controller.getState());
    }

    @Test
    void restart_ClearsBallz() {
        controller.shoot(new Vector2(0, 1));
        controller.update(0.15f);
        controller.restart();
        assertEquals(0, controller.getBallz().size);
    }

    @Test
    void restart_ResetsStartPos() {
        controller.restart();
        assertEquals(3.5f, controller.getStartPos().x, 0.001f);
        assertEquals(GameController.FLOOR_Y, controller.getStartPos().y, 0.001f);
    }

    @Test
    void restart_AllowsShootingAgain() {
        triggerGameOver();
        controller.restart();
        controller.shoot(new Vector2(0, 1));
        assertTrue(controller.isTurnProcessing());
    }

    @Test
    void templateManager_IsCalledOnConstruction() {
        verify(templateManager).loadTemplate("level");
    }

    @Test
    void withTemplate_EnemiesSpawnedFromFirstRow() {
        GridSlot[][] template = {
            {GridSlot.ENEMY, GridSlot.EMPTY, GridSlot.BONUS, GridSlot.EMPTY,
                GridSlot.EMPTY, GridSlot.EMPTY, GridSlot.EMPTY}
        };
        when(templateManager.loadTemplate(anyString())).thenReturn(template);

        GameController ctrl = new GameController(7, 12, factory, templateManager, listener);

        assertFalse(ctrl.getEnemies().isEmpty());
        assertFalse(ctrl.getBonuses().isEmpty());
    }

    private void triggerGameOver() {
        StubEntityFactory lowFactory = new StubEntityFactory() {

            public EnemySquare createEnemy(float x, float y, float size,
                                           int hp, com.badlogic.gdx.graphics.Color color) {
                EnemySquare e = mock(EnemySquare.class);
                when(e.isDead()).thenReturn(false);
                when(e.getY()).thenReturn(GameController.GAME_OVER_Y - 0.1f);
                when(e.getHitbox()).thenReturn(new Rectangle(x, y, size, size));
                return e;
            }

            public Bonus createBonus(float x, float y, float size) {
                return factory.createBonus(x, y, size);
            }
        };

        GridSlot[][] template = {
            {GridSlot.ENEMY, GridSlot.EMPTY, GridSlot.EMPTY,
                GridSlot.EMPTY, GridSlot.EMPTY, GridSlot.EMPTY, GridSlot.EMPTY}
        };
        when(templateManager.loadTemplate(anyString())).thenReturn(template);

        controller = new GameController(7, 12, lowFactory, templateManager, listener);

        controller.shoot(new Vector2(0, 1));
        for (int i = 0; i < 300; i++) {
            controller.update(0.016f);
            if (controller.getState() == GameController.State.GAME_OVER) break;
        }
    }

    private static class StubEntityFactory implements IEntityFactory {
        public EnemySquare createEnemy(float x, float y, float size, int hp,
                                       com.badlogic.gdx.graphics.Color color) {
            EnemySquare enemy = mock(EnemySquare.class);
            when(enemy.isDead()).thenReturn(false);
            when(enemy.getY()).thenReturn(y);
            when(enemy.getHitbox()).thenReturn(new Rectangle(x, y, size, size));
            return enemy;
        }

        public Bonus createBonus(float x, float y, float size) {
            Bonus bonus = mock(Bonus.class);
            when(bonus.getHitbox()).thenReturn(new Rectangle(x, y, size, size));
            when(bonus.getY()).thenReturn(y);
            return bonus;
        }
    }
}
