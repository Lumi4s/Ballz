package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class EnemySquare implements Entity{
    private final Sprite sprite;
    private final Rectangle hitbox;
    private int hp;
    private final Color color;
    private static final GlyphLayout layout = new GlyphLayout();
    private float target_y;
    private BitmapFont font;

    public EnemySquare(Sprite textureSource, float x, float y, float size, int hp, Color color, BitmapFont font) {
        this.hp = hp;
        this.hitbox = new Rectangle(x, y, size, size);

        this.sprite = new Sprite(textureSource);
        this.sprite.setSize(size, size);
        this.sprite.setPosition(x, y);
        this.color = color;
        this.sprite.setColor(color);
        this.font = font;

        this.target_y = y;

    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);

        font.setColor(Color.DARK_GRAY);
        String text = String.valueOf(hp);
        layout.setText(font, text);
        float x = hitbox.x + (hitbox.width - layout.width) / 2f;
        float y = hitbox.y + (hitbox.height + layout.height) / 2f;
        font.draw(batch, layout, x, y);
    }

    public void moveDown() {
        target_y -= 1f;
    }

    public void update(float delta) {
        if (hitbox.y != target_y) {
            float lerpSpeed = 5f;
            hitbox.y = MathUtils.lerp(hitbox.y, target_y, delta * lerpSpeed);

            if (Math.abs(hitbox.y - target_y) < 0.001f) {
                hitbox.y = target_y;
            }
            sprite.setY(hitbox.y);
        }
    }

    public void hurt(int damage) {
        hp -= damage;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public int getHp() {return hp;}

    public float getY() {
        return hitbox.y;
    }

    public Color getColor() {
        return color;
    }
}

