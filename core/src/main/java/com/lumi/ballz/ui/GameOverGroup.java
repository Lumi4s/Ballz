package com.lumi.ballz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lumi.ballz.BallzGame;
import com.lumi.ballz.screens.GameScreen;

public class GameOverGroup extends Table {

    public GameOverGroup(final BallzGame game, final GameScreen screen) {
        setFillParent(true);
        setBackground(game.skin.newDrawable("white", new Color(0, 0, 0, 0.85f)));

        Label title = new Label("GAME OVER", game.skin);
        title.setFontScale(1.5f);
        add(title).padBottom(50);
        row();

        TextButton restartBtn = new TextButton("Try Again", game.skin);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                screen.restartGame();
            }
        });
        add(restartBtn).size(300, 100).padBottom(20);
        row();

        TextButton exitBtn = new TextButton("Menu", game.skin);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                screen.restartGame();
                game.setScreen(game.startingScreen);
            }
        });
        add(exitBtn).size(300, 100);
        setVisible(false);
    }
}
