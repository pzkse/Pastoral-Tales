package com.pz.pastoralTales.farmland;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public class FarmlandPropertyData {

    @SerializedName("base_value")
    private final double baseValue;
    @SerializedName("min_value")
    private final double minValue;
    @SerializedName("max_value")
    private final double maxValue;
    private final double consumption;
    private final Recovery recovery;
    private final Map<String, Double> modifiers;

    private double currentValue;

    public FarmlandPropertyData(double baseValue, double minValue, double maxValue,
                                double consumption, Recovery recovery, Map<String, Double> modifiers) {
        this.baseValue = baseValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.consumption = consumption;
        this.recovery = recovery;
        this.modifiers = modifiers;
        this.currentValue = baseValue;
    }


    public void  calculateValue(Map<String, Double> biome) {
        double value = baseValue;
        for (Map.Entry<String, Double> entry : modifiers.entrySet()) {
            String modifier = entry.getKey();
            if (biome.containsKey(modifier)) {
                value += entry.getValue() * biome.get(modifier);
            }
        }
        setCurrentValue(value);
    }

    public void setCurrentValue(double value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
    }


    public double getCurrentValue() {
        return currentValue;
    }

    public void tick() {
        if (Math.random() < recovery.tickRate) {
            setCurrentValue(currentValue + recovery.amount);
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("CurrentValue", currentValue);
        return tag;
    }

    public void load(CompoundTag tag) {
        this.currentValue = tag.getDouble("CurrentValue");
    }

    public static class Recovery {
        @SerializedName("tick_rate")
        private final double tickRate;
        private final double amount;

        public Recovery(double tickRate, double amount) {
            this.tickRate = tickRate;
            this.amount = amount;
        }
    }
}
