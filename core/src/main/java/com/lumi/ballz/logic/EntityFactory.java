package com.lumi.ballz.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;

public class EntityFactory implements IEntityFactory {

    private static final Color[] COLORS = {
        Color.valueOf("FFB7B2"),
        Color.valueOf("FFDAC1"),
        Color.valueOf("E2F0CB"),
        Color.valueOf("B5EAD7"),
        Color.valueOf("C7CEEA"),
        Color.valueOf("FDFD96"),
        Color.valueOf("FF9AA2")
    };

    private final TextureAtlas atlas;
    private final BitmapFont enemyFont;

    public EntityFactory(TextureAtlas atlas, BitmapFont enemyFont) {
        this.atlas = atlas;
        this.enemyFont = enemyFont;
    }

    public EnemySquare createEnemy(float x, float y, float size, int hp, Color color) {
        Sprite sprite = atlas.createSprite("square");
        Color finalColor = (color != null) ? color : COLORS[MathUtils.random(COLORS.length - 1)];
        return new EnemySquare(sprite, x, y, size, hp, finalColor, enemyFont);
    }

    public Bonus createBonus(float x, float y, float size) {
        Sprite sprite = atlas.createSprite("ui_money");
        return new Bonus(sprite, x, y, size);
    }
}
