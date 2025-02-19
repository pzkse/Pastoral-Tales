package com.pz.pastoralTales.farmland.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.pz.pastoralTales.PastoralTales;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FarmlandConfig {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject properties;

    public static void loadConfig(String modId) {
        // 构建配置文件路径
        Path configPath = Paths.get("config", modId, "farmland_properties.json");

        // 如果配置文件不存在，创建默认配置
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        // 加载配置文件
        try (Reader reader = Files.newBufferedReader(configPath)) {
            properties = JsonParser.parseReader(reader).getAsJsonObject();
            LOGGER.info("Successfully loaded farmland properties config from {}", configPath);
        } catch (IOException e) {
            LOGGER.error("Failed to load farmland properties config: {}", e.getMessage());
            properties = new JsonObject();
        }
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            JsonObject defaultConfig = createDefaultProperties();
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(defaultConfig, writer);
                LOGGER.info("Created default farmland properties config at {}", configPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create default config file: {}", e.getMessage());
        }
    }

    private static JsonObject createDefaultProperties() {
        JsonObject config = new JsonObject();
        JsonObject properties = new JsonObject();

        // 肥力属性
        JsonObject fertility = new JsonObject();
        fertility.addProperty("base_value", 1.0);
        fertility.addProperty("tick_recovery", 0.001); // 每tick恢复率
        fertility.addProperty("recovery_amount", 0.05); // 每次恢复数值
        fertility.addProperty("crop_consumption", 0.1); // 作物生长消耗

        // 水分属性
        JsonObject moisture = new JsonObject();
        moisture.addProperty("base_value", 1.0);
        moisture.addProperty("tick_recovery", 0.002);
        moisture.addProperty("recovery_amount", 0.1);
        moisture.addProperty("crop_consumption", 0.15);

        // 矿物质含量
        JsonObject mineral = new JsonObject();
        mineral.addProperty("base_value", 1.0);
        mineral.addProperty("tick_recovery", 0.0005);
        mineral.addProperty("recovery_amount", 0.03);
        mineral.addProperty("crop_consumption", 0.08);

        properties.add("fertility", fertility);
        properties.add("moisture", moisture);
        properties.add("mineral", mineral);

        config.add("properties", properties);
        return config;
    }

    public static JsonObject getPropertyConfig(String propertyName) {
        if (properties != null &&
                properties.has("properties") &&
                properties.getAsJsonObject("properties").has(propertyName)) {
            return properties.getAsJsonObject("properties")
                    .getAsJsonObject(propertyName);
        }
        return null;
    }

    public static void reload(String modId) {
        loadConfig(modId);
    }
}