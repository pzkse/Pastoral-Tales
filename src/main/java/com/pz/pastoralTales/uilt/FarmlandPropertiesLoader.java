package com.pz.pastoralTales.uilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pz.pastoralTales.farmland.FarmlandProperties;
import com.pz.pastoralTales.farmland.FarmlandProperty;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mojang.text2speech.Narrator.LOGGER;

public class FarmlandPropertiesLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FarmlandProperties properties;

    public static void init() {
        Path configFile = FMLPaths.CONFIGDIR.get().resolve("farmland_properties.json");
        if (!Files.exists(configFile)) {
           return;
        }

        // 加载配置
        loadConfig(configFile);
    }

    private static void loadConfig(Path configFile) {
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            properties = GSON.fromJson(reader, FarmlandProperties.class);
            LOGGER.info("Successfully loaded farmland properties from config");
        } catch (IOException e) {
            LOGGER.error("Failed to load farmland properties config", e);
        }
    }

    public static FarmlandProperties getProperties() {
        return properties;
    }

    // 计算属性值的方法
    public static double calculatePropertyValue(String propertyName, double T, double V, double C, double E, double D, double W) {
        FarmlandProperty property = properties.getProperty(propertyName);
        if (property == null) {
            LOGGER.warn("Property {} not found", propertyName);
            return 0.0;
        }

        double value = property.getBaseValue();
        FarmlandProperty.Modifiers modifiers = property.getModifiers();

        value += T * modifiers.getT();
        value += V * modifiers.getV();
        value += C * modifiers.getC();
        value += E * modifiers.getE();
        value += D * modifiers.getD();
        value += W * modifiers.getW();

        return Math.max(property.getMinValue(), Math.min(property.getMaxValue(), value));
    }

    public static boolean shouldRecover(String propertyName) {
        FarmlandProperty property = properties.getProperty(propertyName);
        if (property == null) return false;

        return Math.random() < property.getRecovery().getTickRate();
    }

    public static double getRecoveryAmount(String propertyName) {
        FarmlandProperty property = properties.getProperty(propertyName);
        return property != null ? property.getRecovery().getAmount() : 0.0;
    }

    public static double getConsumption(String propertyName) {
        FarmlandProperty property = properties.getProperty(propertyName);
        return property != null ? property.getConsumption() : 0.0;
    }
}
