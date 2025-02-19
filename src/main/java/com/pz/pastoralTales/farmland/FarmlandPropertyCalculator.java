package com.pz.pastoralTales.farmland;

import com.google.gson.JsonObject;

public class FarmlandPropertyCalculator {
    private final JsonObject propertyConfig;

    public FarmlandPropertyCalculator(JsonObject config) {
        this.propertyConfig = config;
    }

    public double calculateProperty(String propertyName, BiomeParameters params) {
        if (!propertyConfig.has("properties") ||
                !propertyConfig.getAsJsonObject("properties").has(propertyName)) {
            return 0.0;
        }

        JsonObject property = propertyConfig.getAsJsonObject("properties")
                .getAsJsonObject(propertyName);
        double baseValue = property.get("base_value").getAsDouble();
        double finalValue = baseValue;

        JsonObject modifiers = property.getAsJsonObject("modifiers");

        // 计算每个影响因素的修改值
        if (modifiers.has("T")) {  // 温度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("T"),
                    params.temperature());
        }
        if (modifiers.has("V")) {  // 植被/湿度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("V"),
                    params.humidity());
        }
        if (modifiers.has("C")) {  // 大陆度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("C"),
                    params.continentality());
        }
        if (modifiers.has("E")) {  // 侵蚀度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("E"),
                    params.erosion());
        }
        if (modifiers.has("D")) {  // 深度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("D"),
                    params.depth());
        }
        if (modifiers.has("W")) {  // 奇异度
            finalValue *= calculateModifier(modifiers.getAsJsonObject("W"),
                    params.weirdness());
        }

        // 确保最终值在0-1之间
        return Math.max(0.0, Math.min(1.0, finalValue));
    }

    private double calculateModifier(JsonObject modifier, double paramValue) {
        double influenceWeight = modifier.get("influence_weight").getAsDouble();
        JsonObject ranges = modifier.getAsJsonArray("ranges").get(0).getAsJsonObject();

        double min = ranges.get("min").getAsDouble();
        double max = ranges.get("max").getAsDouble();
        double modifierValue;

        if (ranges.get("modifier").isJsonPrimitive()) {
            modifierValue = ranges.get("modifier").getAsDouble();
        } else {
            // 处理表达式类型的modifier
            String expression = ranges.get("modifier").getAsString();
            modifierValue = evaluateExpression(expression, paramValue);
        }

        // 根据权重计算最终修改值
        return 1.0 + (modifierValue - 1.0) * influenceWeight;
    }

    private double evaluateExpression(String expression, double x) {
        // 这里需要实现表达式解析器
        // 可以使用类似exp4j这样的库来处理数学表达式
        return 1.0; // 临时返回值
    }
}