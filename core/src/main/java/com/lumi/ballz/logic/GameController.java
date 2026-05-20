package com.lumi.ballz.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;
import com.lumi.ballz.entities.ProjectileBall;

public class GameController {
    public static final float FLOOR_Y = 2.15f;
    public static final float GAME_OVER_Y = 3.2f;

    private static final float SPAWN_INTERVAL = 0.1f;
    private static final float BALL_SPEED = 15f;

    private final int ux;
    private final int uy;

    private final Array<EnemySquare> enemies = new Array<>();
    private final Array<ProjectileBall> ballz = new Array<>();
    private final Array<Bonus> bonuses = new Array<>();

    private final Vector2 startPos = new Vector2(3.5f, FLOOR_Y);
    private final Vector2 aimDir = new Vector2();

    private final IEntityFactory factory;
    private final TemplateManager templateManager;
    private final GameEventListener listener;

    private int score = 0;
    private int addBalls = 0;
    private int currentTemplateRow = 0;

    private int ballsToSpawn = 0;
    private float spawnTimer = SPAWN_INTERVAL;
    private boolean turnProcessing = false;
    private boolean firstBallReturned = false;
    private Vector2 nextStartPos;

    private State state = State.PLAYING;
    private GridSlot[][] loadedTemplate;

    public GameController(int ux, int uy,
                          IEntityFactory factory,
                          TemplateManager templateManager,
                          GameEventListener listener) {
        this.ux = ux;
        this.uy = uy;
        this.factory = factory;
        this.templateManager = templateManager;
        this.listener = listener;

        loadedTemplate = templateManager.loadTemplate("level");
        spawnRow();
    }

    public void shoot(Vector2 direction) {
        if (state != State.PLAYING || turnProcessing || ballz.size > 0) return;
        aimDir.set(direction).nor();
        ballsToSpawn = 1 + addBalls;
        turnProcessing = true;
    }

    public void update(float delta) {
        if (state != State.PLAYING) return;

        handleSpawning(delta);
        updateBalls(delta);
        updateEntities(delta);
        removeDeadEnemies();
        DestroyBonusesOutOfBounds();
        checkTurnEnd();
    }

    public void restart() {
        enemies.clear();
        bonuses.clear();
        ballz.clear();

        score = 0;
        addBalls = 0;
        ballsToSpawn = 0;
        currentTemplateRow = 0;
        spawnTimer = SPAWN_INTERVAL;
        turnProcessing = false;
        firstBallReturned = false;
        state = State.PLAYING;
        loadedTemplate = templateManager.loadTemplate("level");

        startPos.set(3.5f, FLOOR_Y);
        spawnRow();
    }

    private void handleSpawning(float delta) {
        if (ballsToSpawn <= 0) return;
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            ballz.add(new ProjectileBall(startPos, aimDir, BALL_SPEED));
            ballsToSpawn--;
            spawnTimer = 0;
        }
    }

    private void updateBalls(float delta) {
        for (int i = ballz.size - 1; i >= 0; i--) {
            ProjectileBall b = ballz.get(i);

            if (b.getStatus() == BallState.FIRE) {
                b.update(delta, ux, uy);
                handleFloorCollision(b);
            } else if (b.getStatus() == BallState.RETURNING) {
                b.moveTo(delta, nextStartPos, BALL_SPEED * 1.25f);
            }

            if (b.getStatus() == BallState.FIRE) {
                checkBallEnemyCollisions(b);
                checkBallBonusCollisions(b);
            }
        }
    }

    private void handleFloorCollision(ProjectileBall b) {
        if (b.getPosition().y < FLOOR_Y) {
            b.getPosition().y = FLOOR_Y;
            if (!firstBallReturned) {
                firstBallReturned = true;
                nextStartPos = new Vector2(b.getPosition().x, FLOOR_Y);
                b.setStatus(BallState.WAITING);
            } else {
                b.setStatus(BallState.RETURNING);
            }
        }
    }

    private void checkBallEnemyCollisions(ProjectileBall b) {
        for (EnemySquare enemy : enemies) {
            b.checkCollision(enemy, 1);
        }
    }

    private void checkBallBonusCollisions(ProjectileBall b) {
        for (int j = bonuses.size - 1; j >= 0; j--) {
            Bonus bonus = bonuses.get(j);
            if (b.getBounds().overlaps(bonus.getHitbox())) {
                addBalls++;
                bonuses.removeIndex(j);
                if (listener != null) listener.onBonusCollected();
            }
        }
    }

    private void updateEntities(float delta) {
        for (EnemySquare e : enemies) e.update(delta);
        for (Bonus b : bonuses) b.update(delta);
    }

    private void removeDeadEnemies() {
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) {
                score += 10;
                enemies.removeIndex(i);
            }
        }
    }

    private void DestroyBonusesOutOfBounds() {
        for (int i = bonuses.size - 1; i >= 0; i--) {
            if (bonuses.get(i).getY() <= GAME_OVER_Y - 1) {
                bonuses.removeIndex(i);
            }
        }
    }

    private void checkTurnEnd() {
        if (!turnProcessing || ballsToSpawn != 0) return;

        for (ProjectileBall b : ballz) {
            if (b.getStatus() != BallState.WAITING) return;
        }
        if (ballz.size == 0) return;

        startPos.set(nextStartPos);
        ballz.clear();
        firstBallReturned = false;

        if (loadedTemplate != null) currentTemplateRow++;
        if (loadedTemplate != null) {
            currentTemplateRow %= loadedTemplate.length;
        }

        spawnRow();
        checkGameOver();

        turnProcessing = false;
        if (listener != null) listener.onTurnEnded();
    }

    private void checkGameOver() {
        for (EnemySquare enemy : enemies) {
            if (enemy.getY() <= GAME_OVER_Y) {
                state = State.GAME_OVER;
                if (listener != null) listener.onGameOver(score);
                return;
            }
        }
    }

    private void clearBonuses() {

    }

    private void spawnRow() {
        if (loadedTemplate != null && currentTemplateRow < loadedTemplate.length) {
            spawnFromTemplate(loadedTemplate[currentTemplateRow]);
        } else {
            spawnRandomRow();
        }
        for (EnemySquare e : enemies) e.moveDown();
        for (Bonus b : bonuses) b.moveDown();
    }

    private void spawnFromTemplate(GridSlot[] row) {
        for (int i = 0; i < ux; i++) {
            if (i >= row.length) break;
            switch (row[i]) {
                case ENEMY:
                    createEnemy(i);
                    break;
                case HARD_ENEMY:
                    createHardEnemy(i);
                    break;
                case BONUS:
                    createBonus(i);
                    break;
                default:
                    break;
            }
        }
    }

    private void spawnRandomRow() {
        Array<Integer> columns = new Array<>();
        for (int i = 0; i < ux; i++) columns.add(i);
        columns.shuffle();

        if (MathUtils.randomBoolean(0.3f)) createBonus(columns.pop());

        int count = MathUtils.random(2, columns.size);
        for (int i = 0; i < count && columns.size > 0; i++) {
            createEnemy(columns.pop());
        }
    }

    private void createEnemy(int col) {
        float size = 0.9f;
        float x = col + (1f - size) / 2f;
        int hp = MathUtils.random(1, 5 + score / 100);
        enemies.add(factory.createEnemy(x, uy - 1, size, hp, null));
    }

    private void createHardEnemy(int col) {
        float size = 0.9f;
        float x = col + (1f - size) / 2f;
        int hp = 15 + score / 5;
        enemies.add(factory.createEnemy(x, uy - 1, size, hp, com.badlogic.gdx.graphics.Color.RED));
    }

    private void createBonus(int col) {
        float size = 0.5f;
        float x = col + (1f - size) / 2f;
        float y = (uy - 1) + (1f - size) / 2f;
        bonuses.add(factory.createBonus(x, y, size));
    }

    public Array<EnemySquare> getEnemies() {
        return enemies;
    }

    public Array<ProjectileBall> getBallz() {
        return ballz;
    }

    public Array<Bonus> getBonuses() {
        return bonuses;
    }

    public Vector2 getStartPos() {
        return startPos;
    }

    public Vector2 getAimDir() {
        return aimDir;
    }

    public int getScore() {
        return score;
    }

    public int getAddBalls() {
        return addBalls;
    }

    public boolean isTurnProcessing() {
        return turnProcessing;
    }

    public State getState() {
        return state;
    }

    public enum State {PLAYING, GAME_OVER}

    public interface GameEventListener {
        void onTurnEnded();

        void onGameOver(int finalScore);

        void onBonusCollected();
    }
}
