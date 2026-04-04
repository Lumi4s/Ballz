package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lumi.ballz.BallzGame;

public class StartingScreen implements Screen {
    private final BallzGame game;

    // Константы мира
    private final int ux = 7;
    private final int uy = 12;

    // Ресурсы
    private Skin skin;
    private TextButton.TextButtonStyle buttonStyle;
    private Stage stage;
    private TextButton button;

    // Камеры и вьюпорты
    private ExtendViewport uiViewport;

    public StartingScreen(BallzGame game) {
        this.game = game;
        uiViewport = new ExtendViewport(720, 1280);

        loadAssets();
        initButtons();
    }

    private void loadAssets(){
        skin = new Skin();
        Pixmap pixmap = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("white", Color.RED);
        buttonStyle.down = skin.newDrawable("white", Color.PURPLE);
        buttonStyle.font = game.uiFont;
    }

    private void initButtons(){
        stage = new Stage(uiViewport);


        button = new TextButton("Enter the game", buttonStyle);
        button.setSize(200, 80);
        button.setPosition(720 / 2f - 100, 1280 / 2f - 40);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.nickname = "damn";
                game.setScreen(game.gameScreen);
            }
        });

        stage.addActor(button);
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.DARK_GRAY);
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        game.batch.end();
    }

    @Override
    public void resize(int width, int height){
        uiViewport.update(width, height, true);
    }

    @Override
    public void hide(){Gdx.input.setInputProcessor(null);}

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public void show(){Gdx.input.setInputProcessor(stage);}

    @Override
    public void dispose() {
    }
}
