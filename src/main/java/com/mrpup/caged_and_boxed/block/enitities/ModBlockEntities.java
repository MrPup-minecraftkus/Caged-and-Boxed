package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CagedandBoxed.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CageBlockEntity>> CAGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cage_block_entity", () ->
                    BlockEntityType.Builder.of(
                            CageBlockEntity::new,
                            ModBlocks.SMALL_CAGE.get(),
                            ModBlocks.MEDIUM_CAGE.get(),
                            ModBlocks.UNIVERSAL_CAGE.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("box_block_entity", () ->
                    BlockEntityType.Builder.of(
                            BoxBlockEntity::new,
                            ModBlocks.BOX_BLOCK.get()
                    ).build(null)
            );
}

