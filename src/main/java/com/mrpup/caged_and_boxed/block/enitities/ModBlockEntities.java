package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<CageBlockEntity> CAGE_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, "cage_block_entity"),
            FabricBlockEntityTypeBuilder.create(
                    CageBlockEntity::new,
                    ModBlocks.SMALL_CAGE,
                    ModBlocks.MEDIUM_CAGE,
                    ModBlocks.UNIVERSAL_CAGE
            ).build()
    );

    public static final BlockEntityType<BoxBlockEntity> BOX_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, "box_block_entity"),
            FabricBlockEntityTypeBuilder.create(
                    BoxBlockEntity::new,
                    ModBlocks.BOX_BLOCK
            ).build()
    );

    public static void register() {
    }
}

