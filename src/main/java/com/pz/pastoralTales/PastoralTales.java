package com.pz.pastoralTales;

import com.mojang.logging.LogUtils;
import com.pz.pastoralTales.registry.ModBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PastoralTales.MODID)
public class PastoralTales {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "pastoral_tales";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();



    public PastoralTales(IEventBus modEventBus, ModContainer modContainer) {

        init(modEventBus,
                ModBlockEntity.BLOCK_ENTITY_TYPES
        );
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private static void init(IEventBus modEventBus,DeferredRegister<?>... deferredRegisters) {
        for (DeferredRegister<?> deferredRegister : deferredRegisters) {
            deferredRegister.register(modEventBus);
        }
    }
}
