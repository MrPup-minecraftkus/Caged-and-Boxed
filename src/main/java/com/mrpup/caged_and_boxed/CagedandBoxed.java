package com.mrpup.caged_and_boxed;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.block.enitities.ModBlockEntities;
import com.mrpup.caged_and_boxed.item.ModItemGroups;
import com.mrpup.caged_and_boxed.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;


@Mod(CagedandBoxed.MOD_ID)
public class CagedandBoxed {

    public static final String MOD_ID = "caged_and_boxed";

    public CagedandBoxed(IEventBus modEventBus, ModContainer modContainer) {

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItemGroups.register(modEventBus);
    }
}
