package com.pz.pastoralTales.block.entity;


import com.pz.pastoralTales.farmland.BiomeParameters;
import com.pz.pastoralTales.farmland.FarmlandProperty;
import com.pz.pastoralTales.farmland.FarmlandPropertyCalculator;
import com.pz.pastoralTales.farmland.config.FarmlandConfig;
import com.pz.pastoralTales.registry.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class FarmBlockEntity extends BlockEntity {
    private final Map<String, FarmlandProperty> properties = new HashMap<>();

    public FarmBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.FARM_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * 初始化耕地属性
     */
    private void initializeProperties() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 获取生物群系参数
        BiomeParameters biomeParams = BiomeParameters.fromLevel(serverLevel, worldPosition);

        // 创建属性计算器
        FarmlandPropertyCalculator calculator = new FarmlandPropertyCalculator(FarmlandConfig.getProperties());

        // 从配置获取所有属性定义并计算初始值
        if (FarmlandConfig.getProperties() != null &&
                FarmlandConfig.getProperties().has("properties")) {
            properties.clear(); // 清除现有属性
            FarmlandConfig.getProperties()
                    .getAsJsonObject("properties")
                    .keySet()
                    .forEach(propertyName -> {
                        // 计算基于生物群系的初始值
                        double calculatedValue = calculator.calculateProperty(propertyName, biomeParams);
                        // 创建属性实例，使用计算后的值作为初始值
                        properties.put(propertyName, new FarmlandProperty(propertyName, calculatedValue));
                    });
        }
    }



    /**
     * 获取指定属性值
     * @param propertyName 属性名称
     * @return 属性值，如果属性不存在返回0
     */
    public double getPropertyValue(String propertyName) {
        return properties.containsKey(propertyName) ?
                properties.get(propertyName).getValue() : 0.0;
    }

    /**
     * 设置属性值
     * @param propertyName 属性名称
     * @param value 新的属性值
     */
    public void setPropertyValue(String propertyName, double value) {
        if (properties.containsKey(propertyName)) {
            properties.get(propertyName).setValue(value);
            setChanged(); // 标记方块实体已更改，需要保存
        }
    }

    /**
     * 当作物生长时消耗属性
     */
    public void onCropGrowth() {
        properties.values().forEach(FarmlandProperty::onCropGrowth);
        setChanged();
    }

    /**
     * 每tick更新属性
     */
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state1, T blockEntity) {
        if (!level.isClientSide() && blockEntity instanceof FarmBlockEntity farmland) {
            // 如果属性为空，初始化属性
            if (farmland.properties.isEmpty()) {
                farmland.initializeProperties();
            }

            // 处理自然恢复
            farmland.properties.values().forEach(FarmlandProperty::tick);
            farmland.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag propertiesTag = new CompoundTag();
        properties.forEach((name, property) ->
                propertiesTag.putDouble(name, property.getValue()));
        tag.put("properties", propertiesTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // 如果存在属性数据，则加载它
        if (tag.contains("Properties")) {
            CompoundTag propertiesTag = tag.getCompound("Properties");

            // 加载每个属性的值
            properties.forEach((name, property) -> {
                if (propertiesTag.contains(name)) {
                    property.setValue(propertiesTag.getDouble(name));
                }
            });
        }
    }



    /**
     * 重置所有属性到初始值
     */
    public void resetProperties() {
        properties.values().forEach(FarmlandProperty::reset);
        setChanged();
    }

    /**
     * 获取所有属性名称
     * @return 属性名称集合
     */
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    /**
     * 检查属性是否存在
     * @param propertyName 属性名称
     * @return 是否存在
     */
    public boolean hasProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    // 获取指定属性
    public FarmlandProperty getProperty(String name) {
        return properties.get(name);
    }
}
