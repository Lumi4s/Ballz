package com.lumi.ballz.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;

public class AIPredictor {
    private final Rectangle virtualBounds = new Rectangle();
    private final Vector2 tempPos = new Vector2();
    private final Vector2 tempVel = new Vector2();

    public Vector2 calculateBestAngle(Vector2 startPos, int ux, int uy, Array<EnemySquare> enemies, Array<Bonus> bonuses) {
        float bestScore = -1;
        float bestAngleDeg = 90;

        // ЭТАП 1: Грубое сканирование (быстро находим перспективные зоны)
        for (float angle = 10; angle <= 170; angle += 4f) {
            float score = simulateShot(startPos, angle, ux, uy, enemies, bonuses);
            if (score > bestScore) {
                bestScore = score;
                bestAngleDeg = angle;
            }
        }

        float startRefined = Math.max(10, bestAngleDeg - 4f);
        float endRefined = Math.min(170, bestAngleDeg + 4f);

        for (float angle = startRefined; angle <= endRefined; angle += 0.5f) {
            float score = simulateShot(startPos, angle, ux, uy, enemies, bonuses);
            if (score > bestScore) {
                bestScore = score;
                bestAngleDeg = angle;
            }
        }

        return new Vector2(1, 0).setAngleDeg(bestAngleDeg);
    }

    private float simulateShot(Vector2 startPos, float angleDeg, int ux, int uy, Array<EnemySquare> enemies, Array<Bonus> bonuses) {
        float score = 0;
        tempPos.set(startPos);
        tempVel.set(1, 0).setAngleDeg(angleDeg).scl(0.15f);

        float radius = 0.15f;
        int maxSteps = 500;

        int[] enemyHp = new int[enemies.size];
        for (int i = 0; i < enemies.size; i++) enemyHp[i] = enemies.get(i).getHp();
        boolean[] bonusCollected = new boolean[bonuses.size];

        int hitsInThisTurn = 0;

        for (int step = 0; step < maxSteps; step++) {
            tempPos.add(tempVel);

            if (tempPos.y > uy * 0.75f) {
                score += 2;
            }

            if (tempPos.x - radius < 0 || tempPos.x + radius > ux) {
                tempVel.x *= -1;
                tempPos.x = MathUtils.clamp(tempPos.x, radius, ux - radius);
                score -= 5;
            }
            if (tempPos.y > uy) {
                tempVel.y *= -1;
                tempPos.y = uy - radius;
            }

            if (tempPos.y < 2.15f) break;

            virtualBounds.set(tempPos.x - radius, tempPos.y - radius, radius * 2, radius * 2);

            for (int i = 0; i < bonuses.size; i++) {
                if (!bonusCollected[i] && virtualBounds.overlaps(bonuses.get(i).getHitbox())) {
                    score += 2000;
                    bonusCollected[i] = true;
                }
            }

            for (int i = 0; i < enemies.size; i++) {
                if (enemyHp[i] <= 0) continue;

                Rectangle hitbox = enemies.get(i).getHitbox();
                if (hitbox.overlaps(virtualBounds)) {
                    reflect(tempPos, tempVel, hitbox, radius);

                    enemyHp[i]--;
                    hitsInThisTurn++;

                    score += (10 + hitsInThisTurn);

                    if (enemyHp[i] <= 0) {
                        score += 100;
                    }

                    if (hitbox.y <= 4.0f) score += 500;

                    break;
                }
            }
        }
        return score;
    }

    private void reflect(Vector2 pos, Vector2 vel, Rectangle hitbox, float radius) {
        float overlapLeft = (pos.x + radius) - hitbox.x;
        float overlapRight = (hitbox.x + hitbox.width) - (pos.x - radius);
        float overlapTop = (hitbox.y + hitbox.height) - (pos.y - radius);
        float overlapBottom = (pos.y + radius) - hitbox.y;

        float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

        if (minOverlap == overlapLeft) {
            vel.x = -Math.abs(vel.x);
            pos.x -= minOverlap;
        } else if (minOverlap == overlapRight) {
            vel.x = Math.abs(vel.x);
            pos.x += minOverlap;
        } else if (minOverlap == overlapBottom) {
            vel.y = -Math.abs(vel.y);
            pos.y -= minOverlap;
        } else if (minOverlap == overlapTop) {
            vel.y = Math.abs(vel.y);
            pos.y += minOverlap;
        }
    }
}
