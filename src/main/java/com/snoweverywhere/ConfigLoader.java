package com.snoweverywhere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toFile();
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "snoweverywhere_config.json");

    public static ModConfig loadConfig() {
        // Ensure the config directory exists
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdir();
        }

        // If the config file does not exist, create it with default values
        if (!CONFIG_FILE.exists()) {
            try {
                createDefaultConfig();
            } catch (IOException e) {
                e.printStackTrace();
                return ModConfig.getDefaultConfig();
            }
        }

        // Read the config file
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, ModConfig.class);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return ModConfig.getDefaultConfig();
        }
    }

    private static void createDefaultConfig() throws IOException {
        ModConfig defaultConfig = ModConfig.getDefaultConfig();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(defaultConfig, writer);
        }
    }

    public static void saveConfig(ModConfig config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}