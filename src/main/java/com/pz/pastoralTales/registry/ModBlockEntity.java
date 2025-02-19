package com.pz.pastoralTales.registry;

import com.pz.pastoralTales.PastoralTales;
import com.pz.pastoralTales.block.entity.FarmBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PastoralTales.MODID);

    public static final Supplier<BlockEntityType<?>> FARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "farm_block_entity",
            () -> BlockEntityType.Builder.of(FarmBlockEntity::new, Blocks.FARMLAND).build(null));


}
