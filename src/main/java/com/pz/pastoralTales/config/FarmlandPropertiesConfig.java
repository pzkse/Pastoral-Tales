package com.pz.pastoralTales.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pz.pastoralTales.PastoralTales;
import com.pz.pastoralTales.farmland.FarmlandPropertyData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FarmlandPropertiesConfig {
    private static final String PATH = "config/pastoral_tales/farmland_properties.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, FarmlandPropertyData> properties = new HashMap<>();

    public void load() {
        File configFile = new File(PATH);
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json.has("properties")) {
                JsonObject propertiesObj = json.getAsJsonObject("properties");
                properties.clear();
                for (String propertyName : propertiesObj.keySet()) {
                    FarmlandPropertyData data = GSON.fromJson(
                        propertiesObj.get(propertyName), FarmlandPropertyData.class
                    );
                    properties.put(propertyName, data);
                }
            }
        }catch (IOException e) {
            PastoralTales.LOGGER.error("Error loading farmland properties config", e);
        }
    }

    private void createDefaultConfig() {
        File configFile = new File("config/pastoral_tales");
        if (!configFile.exists() && !configFile.mkdirs()) {
            PastoralTales.LOGGER.error("Failed to create config directory");
            return;
        }
        JsonObject root = new JsonObject();
        JsonObject properties = new JsonObject();
        addDefaultProperty(properties, "nitrogen", 0.5, 0.0, 1.0, 0.0001,
                0.002, 0.01, Map.of("V", 0.5, "D", 0.3, "W", 0.2));
        root.add("properties", properties);

        try (FileWriter writer = new FileWriter(PATH)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            PastoralTales.LOGGER.error("Failed to create default config", e);
        }
    }
    private void addDefaultProperty(JsonObject properties, String name,
                                    double baseValue, double minValue, double maxValue,
                                    double consumption, double tickRate, double recoveryAmount,
                                    Map<String, Double> modifiers) {
        JsonObject property = new JsonObject();
        property.addProperty("base_value", baseValue);
        property.addProperty("min_value", minValue);
        property.addProperty("max_value", maxValue);
        property.addProperty("consumption", consumption);

        JsonObject recovery = new JsonObject();
        recovery.addProperty("tick_rate", tickRate);
        recovery.addProperty("amount", recoveryAmount);
        property.add("recovery", recovery);

        JsonObject modifiersObj = new JsonObject();
        modifiers.forEach(modifiersObj::addProperty);
        property.add("modifiers", modifiersObj);

        properties.add(name, property);
    }


    public FarmlandPropertyData getProperty(String name) {
        return properties.get(name);
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public Set<String> getPropertyNames() {
        return properties.keySet();
    }
}
