package com.lumi.ballz.logic;

import com.badlogic.gdx.graphics.Color;
import com.lumi.ballz.entities.Bonus;
import com.lumi.ballz.entities.EnemySquare;

public interface IEntityFactory {
    EnemySquare createEnemy(float x, float y, float size, int hp, Color color);

    Bonus createBonus(float x, float y, float size);
}
