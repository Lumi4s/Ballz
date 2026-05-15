package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lumi.ballz.logic.BallState;

public class ProjectileBall {
    private final Vector2 position;
    private Vector2 velocity;
    float radius = 0.15f;
    private final Rectangle bounds = new Rectangle();
    private BallState status = BallState.FIRE;
    private final Vector2 tmpVector = new Vector2();

    public ProjectileBall(Vector2 startPos, Vector2 direction, float speed) {
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(direction).scl(speed);
    }

    public void update(float delta, int ux, int uy) {
        position.add(velocity.x * delta, velocity.y * delta);
        if (position.x - radius < 0 || position.x + radius > ux) {
            velocity.x *= -1;
            position.x = MathUtils.clamp(position.x, radius, ux - radius);
        }
        if (position.y > uy) {
            velocity.y *= -1;
            position.y = uy - radius;
        }
    }

    public void moveTo(float delta, Vector2 target, float speed) {
        tmpVector.set(target).sub(position).nor();
        float distance = position.dst(target);

        float step = speed * delta;
        if (distance <= step) {
            position.set(target);
            status = BallState.WAITING;
        } else {
            position.add(tmpVector.scl(step));
        }
    }

    public void draw(SpriteBatch batch, Sprite ballSprite) {
        batch.draw(
            ballSprite,
            position.x - radius,
            position.y - radius,
            radius * 2,
            radius * 2
        );
    }

    public void checkCollision(EnemySquare enemy, int damageMultiplier) {
        Rectangle enemyHitbox = enemy.getHitbox();
        if (!enemyHitbox.overlaps(this.getBounds())) return;

        float overlapLeft = (position.x + radius) - enemyHitbox.x;
        float overlapRight = (enemyHitbox.x + enemyHitbox.width) - (position.x - radius);
        float overlapTop = (enemyHitbox.y + enemyHitbox.height) - (position.y - radius);
        float overlapBottom = (position.y + radius) - enemyHitbox.y;

        float minOverlap = Math.min(
            Math.min(overlapLeft, overlapRight),
            Math.min(overlapBottom, overlapTop)
        );

        if (minOverlap == overlapLeft) {
            velocity.x = -Math.abs(velocity.x);
            position.x -= minOverlap;
        } else if (minOverlap == overlapRight) {
            velocity.x = Math.abs(velocity.x);
            position.x += minOverlap;
        } else if (minOverlap == overlapBottom) {
            velocity.y = -Math.abs(velocity.y);
            position.y -= minOverlap;
        } else if (minOverlap == overlapTop) {
            velocity.y = Math.abs(velocity.y);
            position.y += minOverlap;
        }

        enemy.hurt(damageMultiplier);
    }

    public void setSpeed(float speed){velocity = velocity.scl(speed);}

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        bounds.set(position.x - radius, position.y - radius, radius * 2, radius * 2);
        return bounds;
    }

    public BallState getStatus(){return status;}
    public void setStatus(BallState status){this.status = status;}
}
