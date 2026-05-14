package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lumi.ballz.BallzGame;

public class LevelEditor implements Screen {
    private final BallzGame game;

    private Stage stage;
    private ExtendViewport uiViewport;
    private Button exitButtonUI;

    public LevelEditor(BallzGame game){
        this.game = game;
        uiViewport = new ExtendViewport(720, 1280);
        stage = new Stage(uiViewport);
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();

        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
