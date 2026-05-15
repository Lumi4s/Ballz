package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lumi.ballz.BallzGame;

public class StartingScreen implements Screen {
    private final BallzGame game;

    private Skin skin;
    private Stage stage;
    private Texture backgroundTexture;
    private ExtendViewport uiViewport;

    private final float BUTTON_WIDTH = 400f;
    private final float BUTTON_HEIGHT = 150f;
    private final float BUTTON_SPACING = 40f;

    public StartingScreen(BallzGame game) {
        this.game = game;
        uiViewport = new ExtendViewport(720, 1280);

        loadAssets();
        initUI();
    }

    private void loadAssets() {
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skin = new Skin();
    }

    private void initUI() {
        stage = new Stage(uiViewport);

        Table table = new Table();
        table.setFillParent(true);

        table.add(createButton("menu_start", () -> game.setScreen(game.gameScreen)))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING);
        table.row();

        table.add(createButton("menu_editor", () -> game.setScreen(game.levelEditor)))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING);
        table.row();

        table.add(createButton("menu_exit", Gdx.app::exit))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT);

        stage.addActor(table);
    }

    private Button createButton(String regionName, Runnable action) {
        TextureRegion region = game.menuAtlas.findRegion(regionName);

        if (region == null) {
            Gdx.app.error("UI", "Region not found: " + regionName);
            return new Button();
        }

        Button button = new Button(new TextureRegionDrawable(region));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, uiViewport.getWorldWidth(), uiViewport.getWorldHeight());
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
        if (skin != null) skin.dispose();
    }
}
