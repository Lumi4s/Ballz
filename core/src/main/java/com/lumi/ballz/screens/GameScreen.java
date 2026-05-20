package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lumi.ballz.BallzGame;
import com.lumi.ballz.logic.*;
import com.lumi.ballz.ui.GameOverGroup;

import java.io.File;

public class GameScreen implements Screen {

    private static final int UX = 7;
    private static final int UY = 12;

    private final BallzGame game;

    private final FitViewport gameViewport;
    private final FitViewport uiViewport;
    private final GameController controller;
    private final GameRenderer renderer;
    private final AIPredictor aiPredictor;
    private final HighScoreManager hsm;
    private final Vector2 touchPos = new Vector2();
    private Stage stage;
    private GameOverGroup gameOverGroup;
    private Button exitButtonUI;
    private Button aiToggleButton;
    private boolean isAiming = false;
    private boolean isAiEnabled = false;

    public GameScreen(BallzGame game) {
        this.game = game;

        gameViewport = new FitViewport(UX, UY);
        uiViewport = new FitViewport(720, 1280);

        hsm = new HighScoreManager(new File("../HighestScore"));
        aiPredictor = new AIPredictor();

        EntityFactory factory = new EntityFactory(game.atlas, game.enemyFont);

        controller = new GameController(UX, UY, factory, new TemplateManager(),
            new GameController.GameEventListener() {
                @Override
                public void onTurnEnded() {
                }

                @Override
                public void onGameOver(int finalScore) {
                    hsm.checkScore(finalScore);
                    gameOverGroup.setVisible(true);
                    gameOverGroup.toFront();
                }

                @Override
                public void onBonusCollected() {
                }
            });

        Texture background = new Texture(Gdx.files.internal("background.png"));
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Sprite ballSprite = game.atlas.createSprite("ball");

        renderer = new GameRenderer(
            game.batch, gameViewport, uiViewport,
            background, ballSprite, game.uiFont, hsm, UX, UY);

        initUI();
    }

    private void initUI() {
        stage = new Stage(uiViewport, game.batch);
        gameOverGroup = new GameOverGroup(game, this);
        stage.addActor(gameOverGroup);
        createExitButton();
        createAiToggleButton();
    }

    private void createExitButton() {
        TextureRegion region = game.atlas.findRegion("ui_exit");
        exitButtonUI = new Button(new TextureRegionDrawable(region));
        exitButtonUI.setSize(100, 106.666f);
        exitButtonUI.setPosition(0, 1280 - 106.666f);
        exitButtonUI.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.startingScreen);
            }
        });
        stage.addActor(exitButtonUI);
    }

    private void createAiToggleButton() {
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(game.atlas.findRegion("ai_off"));
        style.checked = new TextureRegionDrawable(game.atlas.findRegion("ai_on"));
        aiToggleButton = new Button(style);
        aiToggleButton.setPosition(20, 20);
        aiToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isAiEnabled = aiToggleButton.isChecked();
            }
        });
        stage.addActor(aiToggleButton);
    }

    @Override
    public void render(float delta) {
        handleAiTurn();
        controller.update(delta);

        ScreenUtils.clear(Color.BLACK);
        renderer.renderGame(controller, isAiming);
        renderer.renderUI(controller);

        stage.act(delta);
        stage.draw();
    }

    private void handleAiTurn() {
        if (!isAiEnabled) return;
        if (controller.getState() != GameController.State.PLAYING) return;
        if (controller.isTurnProcessing() || controller.getBallz().size > 0 || isAiming) return;

        Vector2 best = aiPredictor.calculateBestAngle(
            controller.getStartPos(), UX, UY,
            controller.getEnemies(), controller.getBonuses());
        Gdx.app.log("AI_BOT", String.format("Shot at angle: %.1f", best.angleDeg()));
        controller.shoot(best);
    }

    private void updateAim(int screenX, int screenY) {
        gameViewport.unproject(touchPos.set(screenX, screenY));
        Vector2 aim = new Vector2(touchPos).sub(controller.getStartPos());
        float angle = aim.angleDeg();
        if (angle < 10 || angle > 270) {
            angle = 10;
        } else if (angle > 170 && angle < 270) {
            angle = 170;
        }
        aim.setAngleDeg(angle).nor();
        controller.getAimDir().set(aim);
    }

    public void restartGame() {
        controller.restart();
        isAiming = false;
        gameOverGroup.setVisible(false);
    }

    @Override
    public void show() {
        restartGame();
        setupInput();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (controller.getState() == GameController.State.PLAYING && !isAiEnabled) {
                    if (controller.getBallz().size == 0) {
                        isAiming = true;
                        updateAim(screenX, screenY);
                    }
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isAiming) updateAim(screenX, screenY);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (controller.getState() == GameController.State.PLAYING && isAiming) {
                    isAiming = false;
                    controller.shoot(controller.getAimDir());
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }
}
