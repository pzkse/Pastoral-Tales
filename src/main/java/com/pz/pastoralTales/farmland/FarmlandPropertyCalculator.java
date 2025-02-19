package com.pz.pastoralTales.farmland;

import com.google.gson.JsonObject;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.graalvm.polyglot.Context;

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
        try {
            // 移除所有空格
            expression = expression.replaceAll("\\s+", "");

            // 替换x为实际值
            expression = expression.replace("x", String.valueOf(x));

            // 计算乘法和除法
            while (expression.contains("*") || expression.contains("/")) {
                expression = calculateMultiplyDivide(expression);
            }

            // 计算加法和减法
            while (expression.contains("+") || expression.contains("-")) {
                expression = calculateAddSubtract(expression);
            }

            return Double.parseDouble(expression);
        } catch (Exception e) {
            LOGGER.error("Error evaluating expression '{}': {}", expression, e.getMessage());
            return 1.0;
        }
    }

    private String calculateMultiplyDivide(String expression) {
        // 查找乘除法表达式
        int opIndex = -1;
        char operator = ' ';

        // 先找乘号
        opIndex = expression.indexOf("*");
        if (opIndex != -1) {
            operator = '*';
        }
        // 如果没有乘号，找除号
        if (opIndex == -1) {
            opIndex = expression.indexOf("/");
            if (opIndex != -1) {
                operator = '/';
            }
        }

        if (opIndex == -1) {
            return expression;
        }

        // 获取操作数
        String leftPart = getLeftOperand(expression, opIndex);
        String rightPart = getRightOperand(expression, opIndex);

        // 计算结果
        double left = Double.parseDouble(leftPart);
        double right = Double.parseDouble(rightPart);
        double result;

        if (operator == '*') {
            result = left * right;
        } else {
            result = left / right;
        }

        // 替换原表达式中的这部分为结果
        return expression.substring(0, opIndex - leftPart.length()) +
                result +
                expression.substring(opIndex + rightPart.length() + 1);
    }

    private String calculateAddSubtract(String expression) {
        // 查找加减法表达式
        int opIndex = -1;
        char operator = ' ';

        // 先找加号
        opIndex = expression.indexOf("+");
        if (opIndex != -1) {
            operator = '+';
        }
        // 如果没有加号，找减号（跳过第一个字符，避免负数）
        if (opIndex == -1) {
            opIndex = expression.indexOf("-", 1);
            if (opIndex != -1) {
                operator = '-';
            }
        }

        if (opIndex == -1) {
            return expression;
        }

        // 获取操作数
        String leftPart = getLeftOperand(expression, opIndex);
        String rightPart = getRightOperand(expression, opIndex);

        // 计算结果
        double left = Double.parseDouble(leftPart);
        double right = Double.parseDouble(rightPart);
        double result;

        if (operator == '+') {
            result = left + right;
        } else {
            result = left - right;
        }

        // 替换原表达式中的这部分为结果
        return expression.substring(0, opIndex - leftPart.length()) +
                result +
                expression.substring(opIndex + rightPart.length() + 1);
    }

    private String getLeftOperand(String expression, int opIndex) {
        StringBuilder left = new StringBuilder();
        int i = opIndex - 1;

        // 向左读取数字和小数点
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) ||
                expression.charAt(i) == '.' ||
                (i == 0 && expression.charAt(i) == '-'))) {
            left.insert(0, expression.charAt(i));
            i--;
        }

        return left.toString();
    }

    private String getRightOperand(String expression, int opIndex) {
        StringBuilder right = new StringBuilder();
        int i = opIndex + 1;

        // 向右读取数字和小数点
        while (i < expression.length() && (Character.isDigit(expression.charAt(i)) ||
                expression.charAt(i) == '.' ||
                (i == opIndex + 1 && expression.charAt(i) == '-'))) {
            right.append(expression.charAt(i));
            i++;
        }

        return right.toString();
    }

}