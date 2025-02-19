package com.pz.pastoralTales.block.entity;


import com.pz.pastoralTales.registry.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
}
