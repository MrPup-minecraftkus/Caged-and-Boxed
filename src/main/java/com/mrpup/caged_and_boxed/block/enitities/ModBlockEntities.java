package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CagedandBoxed.MOD_ID);

    public static final RegistryObject<BlockEntityType<CageBlockEntity>> CAGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cage_block_entity", () ->
                    new BlockEntityType<>(
                            CageBlockEntity::new,
                            Set.of(
                                    ModBlocks.SMALL_CAGE.get(),
                                    ModBlocks.MEDIUM_CAGE.get(),
                                    ModBlocks.UNIVERSAL_CAGE.get()
                            )
                    )
            );

    public static final RegistryObject<BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("box_block_entity", () ->
                    new BlockEntityType<>(
                            BoxBlockEntity::new,
                            Set.of(
                                    ModBlocks.BOX_BLOCK.get()
                            )
                    )
            );
}

