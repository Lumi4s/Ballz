package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Bonus implements Entity {
    private final Sprite sprite;
    private final Rectangle hitbox;
    private float target_y;
    private boolean collected = false;
    private float stateTime = 0;

    public Bonus(Sprite sprite, float x, float y, float size) {
        this.sprite = new Sprite(sprite);
        this.sprite.setSize(size, size);
        this.sprite.setOriginCenter();
        this.sprite.setPosition(x, y);
        this.hitbox = new Rectangle(x, y, size, size);
        this.target_y = y;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        if (hitbox.y != target_y) {
            hitbox.y = MathUtils.lerp(hitbox.y, target_y, delta * 5f);
            if (Math.abs(hitbox.y - target_y) < 0.001f) hitbox.y = target_y;
        }

        float scale = 0.95f + MathUtils.sin(stateTime * 5f) * 0.15f;
        sprite.setScale(scale);
        sprite.setPosition(hitbox.x, hitbox.y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void moveDown() {
        target_y -= 1f;
    }

    public void collect() {
        this.collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    public float getY() {
        return hitbox.y;
    }

}
