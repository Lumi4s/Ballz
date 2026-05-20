package com.lumi.ballz.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import com.lumi.ballz.entities.ProjectileBall;
import com.lumi.ballz.logic.GameController;
import com.lumi.ballz.logic.HighScoreManager;

public class GameRenderer {
    private static final float MAIN_BALL_SIZE = 0.3f;

    private final SpriteBatch batch;
    private final FitViewport gameViewport;
    private final FitViewport uiViewport;
    private final Texture background;
    private final Sprite ballSprite;
    private final BitmapFont uiFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final HighScoreManager hsm;
    private final int ux;
    private final int uy;

    public GameRenderer(SpriteBatch batch,
                        FitViewport gameViewport,
                        FitViewport uiViewport,
                        Texture background,
                        Sprite ballSprite,
                        BitmapFont uiFont,
                        HighScoreManager hsm,
                        int ux, int uy) {
        this.batch = batch;
        this.gameViewport = gameViewport;
        this.uiViewport = uiViewport;
        this.background = background;
        this.ballSprite = ballSprite;
        this.uiFont = uiFont;
        this.hsm = hsm;
        this.ux = ux;
        this.uy = uy;
    }

    public void renderGame(GameController ctrl, boolean isAiming) {
        gameViewport.apply();
        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        batch.begin();

        batch.draw(background, 0, 0, ux, uy);

        for (EnemySquare enemy : ctrl.getEnemies()) enemy.draw(batch);
        for (Bonus bonus : ctrl.getBonuses()) bonus.draw(batch);

        if (!ctrl.isTurnProcessing()) {
            Vector2 sp = ctrl.getStartPos();
            batch.draw(ballSprite,
                sp.x - MAIN_BALL_SIZE / 2f,
                sp.y - MAIN_BALL_SIZE / 2f,
                MAIN_BALL_SIZE, MAIN_BALL_SIZE);
        }

        if (isAiming) {
            Vector2 sp = ctrl.getStartPos();
            Vector2 aim = ctrl.getAimDir();
            for (int i = 1; i <= 3; i++) {
                float dx = sp.x + aim.x * (i * 0.5f);
                float dy = sp.y + aim.y * (i * 0.5f);
                batch.draw(ballSprite, dx - 0.05f, dy - 0.05f, 0.1f, 0.1f);
            }
        }

        for (ProjectileBall b : ctrl.getBallz()) b.draw(batch, ballSprite);

        batch.end();
    }

    public void renderUI(GameController ctrl) {
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();

        uiFont.setColor(Color.WHITE);

        String scoreText = "Score: " + ctrl.getScore();
        layout.setText(uiFont, scoreText);
        uiFont.draw(batch, layout, 720f - layout.width - 20f, 1280f - 40f);

        String bestText = "Best: " + hsm.getHighScore();
        layout.setText(uiFont, bestText);
        uiFont.draw(batch, layout, 360f - layout.width, 1280f - 40f);

        if (!ctrl.isTurnProcessing()) {
            String countText = "x" + (1 + ctrl.getAddBalls());
            layout.setText(uiFont, countText);
            uiFont.draw(batch, layout, 720f / 2f - layout.width / 2f, 120f);
        }

        batch.end();
    }
}
