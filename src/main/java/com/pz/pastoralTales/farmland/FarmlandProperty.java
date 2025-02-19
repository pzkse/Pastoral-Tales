package com.pz.pastoralTales.farmland;


import com.google.gson.JsonObject;
import com.pz.pastoralTales.farmland.config.FarmlandConfig;

public class FarmlandProperty {
    private final String name;
    private double value;
    private final double baseValue;
    private final double tickRecoveryRate;
    private final double recoveryAmount;
    private final double cropConsumption;

    /**
     * 从配置创建耕地属性
     * @param name 属性名称
     * @param defaultValue 默认基础值（如果配置不存在）
     */
    public FarmlandProperty(String name, double defaultValue) {
        this.name = name;

        // 尝试从配置中获取属性设置
        JsonObject config = FarmlandConfig.getPropertyConfig(name);

        if (config != null) {
            // 从配置加载基础值，如果不存在使用默认值
            this.baseValue = Math.max(0.0, Math.min(1.0,
                    config.has("base_value") ?
                            config.get("base_value").getAsDouble() : defaultValue
            ));

            // 加载恢复率和恢复量
            this.tickRecoveryRate = config.has("tick_recovery") ?
                    config.get("tick_recovery").getAsDouble() : 0.0;
            this.recoveryAmount = config.has("recovery_amount") ?
                    config.get("recovery_amount").getAsDouble() : 0.0;

            // 加载作物消耗值
            this.cropConsumption = config.has("crop_consumption") ?
                    config.get("crop_consumption").getAsDouble() : 0.0;
        } else {
            // 如果配置不存在，使用默认值
            this.baseValue = Math.max(0.0, Math.min(1.0, defaultValue));
            this.tickRecoveryRate = 0.0;
            this.recoveryAmount = 0.0;
            this.cropConsumption = 0.0;
        }

        // 初始化当前值为基础值
        this.value = this.baseValue;
    }

    /**
     * 获取属性名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取当前值
     */
    public double getValue() {
        return value;
    }

    /**
     * 设置属性值（确保在0-1范围内）
     */
    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value));
    }

    /**
     * 获取基础值
     */
    public double getBaseValue() {
        return baseValue;
    }

    /**
     * 获取每tick恢复率
     */
    public double getTickRecoveryRate() {
        return tickRecoveryRate;
    }

    /**
     * 获取恢复量
     */
    public double getRecoveryAmount() {
        return recoveryAmount;
    }

    /**
     * 获取作物消耗量
     */
    public double getCropConsumption() {
        return cropConsumption;
    }

    /**
     * 每tick更新
     */
    public void tick() {
        if (tickRecoveryRate > 0 && Math.random() < tickRecoveryRate) {
            setValue(value + recoveryAmount);
        }
    }

    /**
     * 作物生长时消耗
     */
    public void onCropGrowth() {
        if (cropConsumption > 0) {
            setValue(value - cropConsumption);
        }
    }

    /**
     * 重置为基础值
     */
    public void reset() {
        this.value = this.baseValue;
    }

    /**
     * 添加值（可以是负数）
     */
    public void addValue(double amount) {
        setValue(this.value + amount);
    }

    /**
     * 获取属性当前百分比（0-100）
     */
    public double getPercentage() {
        return value * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f%% (base: %.2f, recovery: %.3f/%.2f, consumption: %.2f)",
                name, getPercentage(), baseValue, tickRecoveryRate, recoveryAmount, cropConsumption);
    }
}