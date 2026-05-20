package com.lumi.ballz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lumi.ballz.BallzGame;
import com.lumi.ballz.logic.EditorLogic;
import com.lumi.ballz.logic.GridSlot;
import com.lumi.ballz.logic.TemplateManager;

public class LevelEditor implements Screen {
    private static final int GRID_WIDTH = 7;
    private static final int GRID_HEIGHT = 10;
    private static final Color COLOR_BG = Color.valueOf("121212");
    private static final String LEVEL_NAME = "level";
    private final BallzGame game;
    private final Stage stage;
    private final ExtendViewport uiViewport;
    private final EditorLogic editorLogic;
    private final TemplateManager templateManager;
    private TextButton[][] buttons = new TextButton[GRID_HEIGHT][GRID_WIDTH];

    public LevelEditor(BallzGame game) {
        this.game = game;
        uiViewport = new ExtendViewport(720, 1280);
        stage = new Stage(uiViewport);
        editorLogic = new EditorLogic(GRID_WIDTH, GRID_HEIGHT);
        templateManager = new TemplateManager();

        initUI();
    }

    private void initUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(40);
        stage.addActor(root);

        Table header = new Table();

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.uiFont, Color.WHITE);
        Label title = new Label("EDITOR", labelStyle);
        title.setFontScale(1.5f);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.uiFont;
        textButtonStyle.up = game.skin.getDrawable("white");
        textButtonStyle.fontColor = Color.BLACK;

        TextButton btnInfo = new TextButton("INFO", textButtonStyle);
        btnInfo.getLabel().setFontScale(0.7f);
        btnInfo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showInfoDialog();
            }
        });

        TextButton btnExit = new TextButton("BACK", textButtonStyle);
        btnExit.getLabel().setFontScale(0.7f);
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.startingScreen);
            }
        });

        header.add(title).expandX().left();
        header.add(btnInfo).size(120, 60).right().padRight(10);
        header.add(btnExit).size(120, 60).right();
        root.add(header).fillX().padBottom(40);
        root.row();

        Table gridTable = new Table();
        gridTable.setBackground(game.skin.newDrawable("white", Color.valueOf("1A1A1A")));
        gridTable.pad(10);

        buttons = new TextButton[GRID_HEIGHT][GRID_WIDTH];

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                final int fx = x;
                final int fy = y;

                final TextButton cell = new TextButton("", game.skin);
                cell.getStyle().up = game.skin.newDrawable("white", Color.WHITE);
                cell.setColor(EditorLogic.COLOR_EMPTY);

                cell.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        editorLogic.cycleSlot(fx, fy);
                        updateCellVisual(fx, fy);
                    }
                });

                buttons[y][x] = cell;
                gridTable.add(cell).expand().fill().pad(4).size(80);
            }
            gridTable.row();
        }
        root.add(gridTable).expand().fill().padBottom(40);
        root.row();

        Table footer = new Table();
        TextButton btnClear = new TextButton("CLEAR", textButtonStyle);
        btnClear.getLabel().setFontScale(1.2f);
        TextButton btnSave = new TextButton("SAVE", textButtonStyle);
        btnSave.getLabel().setFontScale(1.2f);

        btnClear.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearGrid();
            }
        });

        btnSave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGrid();
            }
        });

        footer.add(btnClear).size(300, 100).padRight(20);
        footer.add(btnSave).size(300, 100);
        root.add(footer).fillX();
    }

    private void updateCellVisual(int x, int y) {
        GridSlot slot = editorLogic.getSlot(x, y);
        Color color;
        switch (slot) {
            case ENEMY:
                color = EditorLogic.COLOR_ENEMY;
                break;
            case BONUS:
                color = EditorLogic.COLOR_BONUS;
                break;
            case HARD_ENEMY:
                color = EditorLogic.COLOR_HARD;
                break;
            default:
                color = EditorLogic.COLOR_EMPTY;
                break;
        }
        buttons[y][x].setColor(color);
    }

    private void clearGrid() {
        editorLogic.clear();
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                buttons[y][x].setColor(EditorLogic.COLOR_EMPTY);
            }
        }
    }

    public void saveGrid() {
        templateManager.saveTemplate(editorLogic.getGrid(), LEVEL_NAME);
        showMessage();
    }

    private void showMessage() {
        final Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(game.skin.newDrawable("white", new Color(0, 0, 0, 0.6f)));
        overlay.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        Table dialog = new Table();
        dialog.setBackground(game.skin.newDrawable("white", Color.valueOf("333333")));
        dialog.pad(40);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.uiFont, Color.GOLD);

        dialog.add(new Label("Success", titleStyle)).padBottom(20).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.uiFont;
        btnStyle.up = game.skin.getDrawable("white");
        btnStyle.fontColor = Color.BLACK;

        TextButton okBtn = new TextButton("OK", btnStyle);
        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        dialog.add(okBtn).size(150, 60);

        overlay.add(dialog).width(400);
        stage.addActor(overlay);
    }

    private void showInfoDialog() {
        final Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(game.skin.newDrawable("white", new Color(0, 0, 0, 0.8f)));

        overlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        Table dialog = new Table();
        dialog.setBackground(game.skin.newDrawable("white", Color.valueOf("2A2A2A")));
        dialog.pad(30);

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.uiFont, Color.WHITE);
        Label titleLabel = new Label("COLOR LEGEND", labelStyle);
        titleLabel.setFontScale(1f);
        dialog.add(titleLabel).colspan(2).padBottom(30).row();

        addLegendRow(dialog, EditorLogic.COLOR_ENEMY, "Enemy", labelStyle);
        addLegendRow(dialog, EditorLogic.COLOR_BONUS, "+1 Ball Bonus", labelStyle);
        addLegendRow(dialog, EditorLogic.COLOR_HARD, "Hard Enemy", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.uiFont;
        btnStyle.up = game.skin.newDrawable("white", Color.WHITE);
        btnStyle.fontColor = Color.BLACK;

        TextButton closeBtn = new TextButton("CLOSE", btnStyle);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        dialog.add(closeBtn).colspan(2).size(200, 80).padTop(30);

        overlay.add(dialog).width(500);
        stage.addActor(overlay);
    }

    private void addLegendRow(Table table, Color color, String desc, Label.LabelStyle style) {
        Table colorBox = new Table();
        colorBox.setBackground(game.skin.newDrawable("white", color));

        Label textLabel = new Label(desc, style);

        table.add(colorBox).size(40).padBottom(15).padRight(20);
        table.add(textLabel).left().expandX().padBottom(15).row();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(COLOR_BG);
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
