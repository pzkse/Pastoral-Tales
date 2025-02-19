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
        // 肥力属性配置
        properties.add("fertility", createPropertyConfig(
                1.0,  // base_value
                0.001, // tick_recovery
                0.05,  // recovery_amount
                0.1,   // crop_consumption
                new ModifierConfig[]{
                        new ModifierConfig("T", 0.3, "0.8 + (x * 0.4)"),
                        new ModifierConfig("V", 0.4, "0.6 + (x * 0.8)"),
                        new ModifierConfig("E", 0.3, "1.2 - (x * 0.4)")
                }
        ));

        // 水分属性配置
        properties.add("moisture", createPropertyConfig(
                1.0,   // base_value
                0.002, // tick_recovery
                0.1,   // recovery_amount
                0.15,  // crop_consumption
                new ModifierConfig[]{
                        new ModifierConfig("T", 0.4, "1.0 - (x * 0.6)"),
                        new ModifierConfig("V", 0.4, "0.5 + (x * 1.0)"),
                        new ModifierConfig("C", 0.2, "1.0 - (Math.abs(x) * 0.4)")
                }
        ));

        // 矿物质属性配置
        properties.add("mineral", createPropertyConfig(
                1.0,    // base_value
                0.0005, // tick_recovery
                0.03,   // recovery_amount
                0.08,   // crop_consumption
                new ModifierConfig[]{
                        new ModifierConfig("E", 0.5, "0.7 + (x * 0.6)"),
                        new ModifierConfig("D", 0.3, "0.8 + (x * 0.4)"),
                        new ModifierConfig("W", 0.2, "1.0 + (Math.abs(x) * 0.2)")
                }
        ));

        config.add("properties", properties);
        return config;
    }

    private static JsonObject createPropertyConfig(
            double baseValue,
            double tickRecovery,
            double recoveryAmount,
            double cropConsumption,
            ModifierConfig[] modifiers
    ) {
        JsonObject property = new JsonObject();
        property.addProperty("base_value", baseValue);
        property.addProperty("tick_recovery", tickRecovery);
        property.addProperty("recovery_amount", recoveryAmount);
        property.addProperty("crop_consumption", cropConsumption);

        JsonObject modifiersObject = new JsonObject();
        for (ModifierConfig modifier : modifiers) {
            JsonObject modifierObject = new JsonObject();
            modifierObject.addProperty("influence_weight", modifier.weight);

            JsonObject rangeObject = new JsonObject();
            rangeObject.addProperty("min", -1.0);
            rangeObject.addProperty("max", 1.0);
            rangeObject.addProperty("modifier", modifier.expression);

            JsonObject ranges = new JsonObject();
            modifierObject.add("ranges", GSON.toJsonTree(new JsonObject[]{rangeObject}));

            modifiersObject.add(modifier.type, modifierObject);
        }

        property.add("modifiers", modifiersObject);
        return property;
    }

    private static class ModifierConfig {
        final String type;
        final double weight;
        final String expression;

        ModifierConfig(String type, double weight, String expression) {
            this.type = type;
            this.weight = weight;
            this.expression = expression;
        }
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