package com.pz.pastoralTales.block.entity;


import com.pz.pastoralTales.PastoralTales;
import com.pz.pastoralTales.registry.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FarmBlockEntity extends BlockEntity {

    public FarmBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.FARM_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * 每tick更新属性
     */
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state1, T blockEntity) {

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

    }







    // 获取生物群系参数
    private Map<String, Double> getBiomeParameters() {
        Map<String, Double> parameters = new HashMap<>();
        if (!(level instanceof ServerLevel serverLevel)) return parameters;
        ServerChunkCache serverchunkcache = serverLevel.getChunkSource();
        RandomState random = serverchunkcache.randomState();
        // 计算采样点
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();
        DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(x, y, z);
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        NoiseRouter noiserouter = random.router();
        try {
            parameters.put("T", Double.valueOf(decimalformat.format(noiserouter.temperature().compute(context))));
            parameters.put("V", Double.valueOf(decimalformat.format(noiserouter.vegetation().compute(context))));
            parameters.put("C", Double.valueOf(decimalformat.format(noiserouter.continents().compute(context))));
            parameters.put("E", Double.valueOf(decimalformat.format(noiserouter.erosion().compute(context))));
            parameters.put("D", Double.valueOf(decimalformat.format(noiserouter.depth().compute(context))));
            parameters.put("W", noiserouter.ridges().compute(context));
        } catch (NumberFormatException e) {
            PastoralTales.LOGGER.error("Error computing biome parameters", e);
        }
        return parameters;
    }
}
