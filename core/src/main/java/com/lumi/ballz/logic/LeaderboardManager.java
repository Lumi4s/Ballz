package com.lumi.ballz.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {
    private final ObjectMapper mapper = new ObjectMapper();

    private FileHandle getFileHandle() {
        return Gdx.files.local("../leaderboard.json");
    }

    private void save(List<PlayerScore> scores) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(getFileHandle().file(), scores);
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "save error", e);
        }
    }

    public List<PlayerScore> load() {
        FileHandle handle = getFileHandle();
        try {
            if (!handle.exists()) return new ArrayList<>();
            return mapper.readValue(handle.file(), new TypeReference<List<PlayerScore>>() {
            });
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "load error", e);
            return new ArrayList<>();
        }
    }

    public void addScore(PlayerScore newScore) {
        List<PlayerScore> scores = load();
        scores.add(newScore);
        scores.sort((p1, p2) -> Integer.compare(p2.score, p1.score));
        if (scores.size() > 10) {
            scores = new ArrayList<>(scores.subList(0, 10));
        }
        save(scores);
    }
}
