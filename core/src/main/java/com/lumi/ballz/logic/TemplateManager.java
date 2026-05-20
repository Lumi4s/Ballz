package com.lumi.ballz.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TemplateManager {
    private final Json json;
    private final String baseDir;

    public TemplateManager() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);

        String path = "templates/";
        if (Gdx.files.local("templates/").exists()) {
            path = "templates/";
        }
        this.baseDir = path;

        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public TemplateManager(String baseDir) {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.baseDir = baseDir;

        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveTemplate(GridSlot[][] grid, String templateName) {
        File file = new File(baseDir + templateName + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            String data = json.prettyPrint(grid);
            writer.write(data);
        } catch (IOException e) {
            System.err.println("TemplateManager save error: " + e.getMessage());
        }
    }

    public GridSlot[][] loadTemplate(String templateName) {
        File file = new File(baseDir + templateName + ".json");
        if (file.exists()) {
            try {
                byte[] encoded = Files.readAllBytes(file.toPath());
                String content = new String(encoded, StandardCharsets.UTF_8);
                return json.fromJson(GridSlot[][].class, content);
            } catch (IOException e) {
                System.err.println("TemplateManager load error: " + e.getMessage());
            }
        }
        return null;
    }
}
