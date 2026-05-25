package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<CageBlockEntity> CAGE_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, "cage_block_entity"),
            BlockEntityType.Builder.of(
                    CageBlockEntity::new,
                    ModBlocks.SMALL_CAGE,
                    ModBlocks.MEDIUM_CAGE,
                    ModBlocks.UNIVERSAL_CAGE
            ).build(null)
    );

    public static final BlockEntityType<BoxBlockEntity> BOX_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, "box_block_entity"),
            BlockEntityType.Builder.of(
                    BoxBlockEntity::new,
                    ModBlocks.BOX_BLOCK
            ).build(null)
    );

    public static void register() {
    }
}

