package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lumi.ballz.BallzGame;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import com.lumi.ballz.entities.ProjectileBall;
import com.lumi.ballz.logic.BallState;
import com.lumi.ballz.logic.PlayerScore;
import com.lumi.ballz.ui.GameOverGroup;
import com.lumi.ballz.logic.LeaderboardManager;


public class GameScreen implements Screen {
    private final BallzGame game;

    // Константы мира
    private final int ux = 7;
    private final int uy = 12;

    private final float spawnInterval = 0.1f;
    private final float ballSpeed = 15f;

    // Ресурсы
    private Sprite enemySprite, ballSprite, bonusSprite;
    private BitmapFont enemyFont, uiFont;
    private GlyphLayout uiLayout;
    private Texture backgroundTexture;
    private Button exitButtonUI;

    // Камеры и вьюпорты
    private FitViewport gameViewport;
    private FitViewport uiViewport;
    private Stage stage;
    private GameOverGroup gameOverGroup;

    // Состояние игры
    private Array<EnemySquare> enemies;
    private Array<ProjectileBall> ballz;
    private Array<Bonus> bonuses;
    private Vector2 startPos = new Vector2(3.5f, 2.15f);
    private Vector2 nextStartPos;
    private Vector2 aimPos = new Vector2();
    private Vector2 touchPos = new Vector2();
    private LeaderboardManager LM;

    private boolean isAiming = false;
    private boolean turnProcessing = false;
    private boolean firstBallReturned;
    private int score = 0;
    private int addBalls = 0;
    private int ballsToSpawn = 0;
    private float spawnTimer = 0f;
    private float afkTimer = 0f;

    private enum State {
        PLAYING, GAME_OVER
    }

    private State state = State.PLAYING;

    private final Color[] colors = {Color.valueOf("FFB7B2"), // Нежно-розовый
        Color.valueOf("FFDAC1"), // Персиковый
        Color.valueOf("E2F0CB"), // Мятно-лаймовый
        Color.valueOf("B5EAD7"), // Аквамарин
        Color.valueOf("C7CEEA"), // Лавандово-голубой
        Color.valueOf("FDFD96"), // Пастельно-желтый
        Color.valueOf("FF9AA2")  // Коралловый пастель
    };

    public GameScreen(BallzGame game) {
        this.game = game;

        gameViewport = new FitViewport(ux, uy);
        uiViewport = new FitViewport(720, 1280);

        loadAssets();
        initLogic();
        initUI();
    }

    private void initUI() {
        stage = new Stage(uiViewport, game.batch);
        gameOverGroup = new GameOverGroup(game, this);
        stage.addActor(gameOverGroup);
        createExitButton();
    }

    private void createExitButton() {
        TextureRegion region = game.atlas.findRegion("ui_exit");
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        exitButtonUI = new Button(drawable);
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

    private void loadAssets() {
        enemySprite = game.atlas.createSprite("square");
        ballSprite = game.atlas.createSprite("ball");
        bonusSprite = game.atlas.createSprite("ui_money");
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        enemyFont = game.enemyFont;
        uiFont = game.uiFont;
        uiLayout = new GlyphLayout();
    }

    private void initLogic() {
        enemies = new Array<>();
        ballz = new Array<>();
        bonuses = new Array<>();
        LM = new LeaderboardManager();
        spawnRow();
    }

    private void updateAim(int screenX, int screenY) {
        gameViewport.unproject(touchPos.set(screenX, screenY));
        aimPos.set(touchPos).sub(startPos);
        float angle = aimPos.angleDeg();
        if (angle < 10 || angle > 270) angle = 10;
        else if (angle > 170 && angle < 270) angle = 170;
        aimPos.setAngleDeg(angle).nor();
    }

    private void spawnRow() {
        Array<Integer> columns = new Array<>();
        for (int i = 0; i < ux; i++) columns.add(i);
        columns.shuffle();

        if (MathUtils.randomBoolean(0.3f)) {
            createBonus(columns.pop());
        }

        int enemyCount = MathUtils.random(2, columns.size);
        for (int i = 0; i < enemyCount; i++) {
            if (columns.size > 0) {
                createEnemy(columns.pop());
            }
        }
        for (EnemySquare enemy : enemies) enemy.moveDown();
        for (Bonus bonus : bonuses) bonus.moveDown();

    }

    private void createEnemy(int index) {
        float size = 0.9f;
        float spawnX = index + (1f - size) / 2f;
        int hp = MathUtils.random(1, 5 + score / 100);
        Color color = colors[MathUtils.random(colors.length - 1)];
        enemies.add(new EnemySquare(enemySprite, spawnX, uy - 1, size, hp, color, enemyFont));
    }

    private void createBonus(int index) {
        float size = 0.5f;
        float spawnX = index + (1f - size) / 2f;
        float spawnY = (uy - 1) + (1f - size) / 2f;
        bonuses.add(new Bonus(bonusSprite, spawnX, spawnY, size));
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(Color.BLACK);

        // Game
        gameViewport.apply();
        game.batch.setProjectionMatrix(gameViewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, ux, uy);

        // Враги
        for (EnemySquare enemy : enemies) enemy.draw(game.batch);

        // Бонусы
        for (Bonus bonus : bonuses) bonus.draw(game.batch);

        // Прицел
        if (!turnProcessing) {
            float mainBallSize = 0.3f;
            game.batch.draw(ballSprite, startPos.x - mainBallSize / 2f, startPos.y - mainBallSize / 2f, mainBallSize, mainBallSize);
        }
        if (isAiming) {
            for (int i = 1; i <= 3; i++) {
                float dx = startPos.x + aimPos.x * (i * 0.5f);
                float dy = startPos.y + aimPos.y * (i * 0.5f);
                game.batch.draw(ballSprite, dx - 0.05f, dy - 0.05f, 0.1f, 0.1f);
            }
        }

        // Шары
        for (ProjectileBall b : ballz) b.draw(game.batch, ballSprite);
        game.batch.end();


        // UI
        uiViewport.apply();
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();
        drawUI();
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void drawUI() {
        // Очки
        uiFont.setColor(Color.WHITE);
        String scoreText = String.valueOf(score);
        uiLayout.setText(uiFont, scoreText);
        uiFont.draw(game.batch, uiLayout, 720f - uiLayout.width - 20f, 1280f - 40f);

        // Количество шаров у пушки
        if (!turnProcessing) {
            String countText = "x" + (1 + addBalls);
            uiLayout.setText(uiFont, countText);
            uiFont.draw(game.batch, uiLayout, 720f / 2f - uiLayout.width / 2f, 120f);
        }
    }

    private void update(float delta) {
        // Спавн шаров по таймеру
        if (ballsToSpawn > 0) {
            spawnTimer += delta;
            if (spawnTimer >= spawnInterval) {

                ballz.add(new ProjectileBall(startPos, aimPos, ballSpeed));
                ballsToSpawn--;
                spawnTimer = 0;
            }
        } else {
            spawnTimer = spawnInterval;
        }

        // Логика шаров
        for (int i = ballz.size - 1; i >= 0; i--) {
            ProjectileBall b = ballz.get(i);
            if (b.getStatus().equals(BallState.FIRE)) {
                b.update(delta, ux, uy);

                if (b.getPosition().y < 2.15f) {
                    b.getPosition().y = 2.15f;

                    if (!firstBallReturned) {
                        firstBallReturned = true;
                        nextStartPos = new Vector2(b.getPosition().x, 2.15f);
                        b.setStatus(BallState.WAITING);
                    } else {
                        b.setStatus(BallState.RETURNING);
                    }
                }
            } else if (b.getStatus().equals(BallState.RETURNING)) {
                b.moveTo(delta, nextStartPos, ballSpeed * 1.25f);
            }

            if (b.getStatus().equals(BallState.FIRE)) {
                for (EnemySquare enemy : enemies) {
                    b.checkCollision(enemy, 1);
                }

                for (int j = bonuses.size - 1; j >= 0; j--) {
                    Bonus bonus = bonuses.get(j);
                    if (b.getBounds().overlaps(bonus.getHitbox())) {
                        addBalls++;
                        bonuses.removeIndex(j);
                        // Эффекты или звуки
                    }
                }
            }
        }

        // Обновление каждого врага и бонуса
        for (EnemySquare enemy : enemies) {
            enemy.update(delta);
        }
        for (Bonus bonus : bonuses) {
            bonus.update(delta);
        }

        // Проверка смерти врагов
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) {
                score += 10;
                enemies.removeIndex(i);
            }
        }
        checkTurnEnd();
    }

    private void checkTurnEnd() {
        if (turnProcessing && ballsToSpawn == 0) {
            boolean allFinished = true;
            for (ProjectileBall b : ballz) {
                if (!b.getStatus().equals(BallState.WAITING)) {
                    allFinished = false;
                    break;
                }
            }

            if (allFinished && ballz.size > 0) {
                startPos.set(nextStartPos);
                ballz.clear();
                firstBallReturned = false;

                spawnRow();
                checkGameOver();

                turnProcessing = false;
            }
        }
    }

    private void checkGameOver() {
        for (EnemySquare enemy : enemies) {
            if (enemy.getY() <= 3.2f) {
                LM.addScore(new PlayerScore(game.nickname, score));
                state = State.GAME_OVER;
                gameOverGroup.setVisible(true);
                gameOverGroup.toFront();
                return;
            }
        }
    }

    public void restartGame() {
        enemies.clear();
        bonuses.clear();
        ballz.clear();
        score = 0;
        addBalls = 0;
        ballsToSpawn = 0;
        firstBallReturned = false;
        isAiming = false;
        turnProcessing = false;

        startPos.set(3.5f, 2.15f);
        spawnRow();

        state = State.PLAYING;
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        setupInput();
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (state == State.PLAYING) {
                    if (ballz.size == 0) {
                        isAiming = true;
                        updateAim(screenX, screenY);
                    }
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (state == State.PLAYING) {
                    if (isAiming) updateAim(screenX, screenY);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (state == State.PLAYING) {
                    if (ballz.size == 0 && isAiming) {
                        isAiming = false;
                        ballsToSpawn = 1 + addBalls;
                        turnProcessing = true;
                    }
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
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


}
