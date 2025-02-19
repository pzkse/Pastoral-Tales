package com.pz.pastoralTales.farmland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;

import java.text.DecimalFormat;

public record BiomeParameters(
        double temperature,
        double humidity,
        double continentality,
        double erosion,
        double depth,
        double weirdness
) {
    private static final DecimalFormat decimalformat = new DecimalFormat("0.000");
    public static BiomeParameters fromLevel(ServerLevel level, BlockPos pos) {
        ServerChunkCache serverChunkCache = level.getChunkSource();
        RandomState randomState = serverChunkCache.randomState();
        NoiseRouter noiseRouter = randomState.router();
        DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        double temperature = Double.parseDouble(decimalformat.format(noiseRouter.temperature().compute(context)));
        double humidity = Double.parseDouble(decimalformat.format(noiseRouter.vegetation().compute(context)));
        double continentality = Double.parseDouble(decimalformat.format(noiseRouter.continents().compute(context)));
        double erosion = Double.parseDouble(decimalformat.format(noiseRouter.erosion().compute(context)));
        double depth = Double.parseDouble(decimalformat.format(noiseRouter.depth().compute(context)));
        double weirdness = Double.parseDouble(decimalformat.format(noiseRouter.ridges().compute(context)));
        return new BiomeParameters(
                temperature,
                humidity,
                continentality,
                erosion,
                depth,
                weirdness
        );
    }
}