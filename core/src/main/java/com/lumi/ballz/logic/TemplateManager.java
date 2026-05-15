package com.lumi.ballz.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

public class TemplateManager {
    private static final String SAVE_PATH = "templates/level_patterns.json";
    private Json json;

    public TemplateManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void saveTemplate(GridSlot[][] grid, String templateName) {
        try {
            FileHandle file = Gdx.files.local("templates/" + templateName + ".json");
            String data = json.prettyPrint(grid);
            file.writeString(data, false);
            Gdx.app.log("TemplateManager", "Saved to: " + file.path());
        } catch (Exception e) {
            Gdx.app.error("TemplateManager", "Save failed", e);
        }
    }

    public GridSlot[][] loadTemplate(String templateName) {
        FileHandle file = Gdx.files.local("templates/" + templateName + ".json");
        if (file.exists()) {
            try {
                return json.fromJson(GridSlot[][].class, file.readString());
            } catch (SerializationException e) {
                Gdx.app.error("TemplateManager", "Load failed", e);
            }
        }
        return null;
    }
}
