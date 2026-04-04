package com.lumi.ballz.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface Entity {

    void draw(SpriteBatch batch);

    void moveDown();

    void update(float delta);

    Rectangle getHitbox();

    float getY();
}

