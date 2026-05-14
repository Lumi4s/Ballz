package com.lumi.ballz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lumi.ballz.screens.GameScreen;
import com.lumi.ballz.screens.LevelEditor;
import com.lumi.ballz.screens.StartingScreen;


public class BallzGame extends Game {
    public TextureAtlas atlas;
    public TextureAtlas menuAtlas;
    public SpriteBatch batch;
    public StartingScreen startingScreen;
    public GameScreen gameScreen;
    public LevelEditor levelEditor;
    public BitmapFont enemyFont;
    public BitmapFont uiFont;
    public Skin skin;
    public String nickname;

    @Override
    public void render() {
        super.render();
    }


    @Override
    public void create() {
        loadAssets();
        batch = new SpriteBatch();

        startingScreen = new StartingScreen(this);
        gameScreen = new GameScreen(this);
        levelEditor = new LevelEditor(this);

        this.setScreen(startingScreen);
    }

    private void loadAssets(){
        atlas = new TextureAtlas(Gdx.files.internal("atlas/pack.atlas"));
        menuAtlas = new TextureAtlas(Gdx.files.internal("atlas/menu_pack.atlas"));

        enemyFont = new BitmapFont(Gdx.files.internal("font.fnt"));
        enemyFont.setUseIntegerPositions(false);
        enemyFont.getData().setScale(0.006f);
        enemyFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        uiFont = new BitmapFont(Gdx.files.internal("font.fnt"));
        uiFont.getData().setScale(0.8f);
        uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        skin = new Skin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = skin.newDrawable("white", Color.RED);
        btnStyle.down = skin.newDrawable("white", Color.PURPLE);
        btnStyle.font = uiFont;
        skin.add("default", btnStyle);

        Label.LabelStyle lblStyle = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle();
        lblStyle.font = uiFont;
        skin.add("default", lblStyle);

    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        enemyFont.dispose();
        uiFont.dispose();
        skin.dispose();
    }
}
