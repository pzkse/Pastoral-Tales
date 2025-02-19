package com.pz.pastoralTales.farmland;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmlandPropertyCalculator {
    private final JsonObject propertyConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(FarmlandPropertyCalculator.class);

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
        // 获取修改器值
        JsonElement modifierElement = ranges.get("modifier");
        double modifierValue;

        if (modifierElement.isJsonPrimitive() && !modifierElement.getAsString().contains("x")) {
            // 如果是简单数值
            modifierValue = modifierElement.getAsDouble();
        } else {
            // 如果是表达式
            String expression = modifierElement.getAsString();
            modifierValue = calculateExpressionValue(expression, paramValue);
        }

        // 限制参数值在范围内
        paramValue = Math.max(min, Math.min(max, paramValue));

        return 1.0 + (modifierValue - 1.0) * influenceWeight;
    }
    private double calculateExpressionValue(String expression, double x) {
        try {
            // 移除所有空格
            expression = expression.replaceAll("\\s+", "");

            // 替换x为实际值
            expression = expression.replace("x", String.valueOf(x));

            // 替换数学函数
            if (expression.contains("Math.abs")) {
                expression = expression.replace("Math.abs(", "abs(");
            }

            // 使用简单的表达式计算
            return evaluateSimpleExpression(expression);
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate expression: {} with x={}", expression, x, e);
            return 1.0;
        }
    }


    private double evaluateSimpleExpression(String expression) {
        // 移除括号，同时保留括号内的内容
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf("(");
            int closeIndex = expression.indexOf(")", openIndex);
            if (closeIndex == -1) throw new IllegalArgumentException("Mismatched parentheses");

            String subExpr = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateSimpleExpression(subExpr);

            expression = expression.substring(0, openIndex) +
                    subResult +
                    expression.substring(closeIndex + 1);
        }

        // 处理abs函数
        if (expression.startsWith("abs")) {
            String argument = expression.substring(3);
            return Math.abs(evaluateSimpleExpression(argument));
        }

        // 分割表达式
        String[] parts;
        double result;

        if (expression.contains("+")) {
            parts = expression.split("\\+");
            result = evaluateSimpleExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result += evaluateSimpleExpression(parts[i]);
            }
        } else if (expression.contains("-")) {
            parts = expression.split("-");
            result = evaluateSimpleExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result -= evaluateSimpleExpression(parts[i]);
            }
        } else if (expression.contains("*")) {
            parts = expression.split("\\*");
            result = evaluateSimpleExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result *= evaluateSimpleExpression(parts[i]);
            }
        } else if (expression.contains("/")) {
            parts = expression.split("/");
            result = evaluateSimpleExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result /= evaluateSimpleExpression(parts[i]);
            }
        } else {
            // 如果是纯数字
            result = Double.parseDouble(expression);
        }

        return result;
    }

}