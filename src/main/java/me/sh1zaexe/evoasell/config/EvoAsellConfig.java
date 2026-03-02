package me.sh1zaexe.evoasell.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

/**
 * Persistent JSON config for EvoAsell mod.
 * Stored at: .minecraft/config/evoasell.json
 */
public class EvoAsellConfig {

    // ── AutoMessage settings ──────────────────────────────────────────────────
    public boolean autoMessageEnabled = false;
    public String  autoMessageText    = "Hello! EvoAsell is active!";
    /** Interval in seconds between each auto-message send (default 60 s). */
    public int     autoMessageInterval = 60;

    // ── Internal (not serialised) ─────────────────────────────────────────────
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("evoasell.json");

    private static EvoAsellConfig INSTANCE;

    // ─────────────────────────────────────────────────────────────────────────
    public static EvoAsellConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    // ─────────────────────────────────────────────────────────────────────────
    public static EvoAsellConfig load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                EvoAsellConfig cfg = GSON.fromJson(reader, EvoAsellConfig.class);
                INSTANCE = (cfg != null) ? cfg : new EvoAsellConfig();
                return INSTANCE;
            } catch (IOException e) {
                System.err.println("[EvoAsell] Failed to load config: " + e.getMessage());
            }
        }
        INSTANCE = new EvoAsellConfig();
        INSTANCE.save();
        return INSTANCE;
    }

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("[EvoAsell] Failed to save config: " + e.getMessage());
        }
    }
}
