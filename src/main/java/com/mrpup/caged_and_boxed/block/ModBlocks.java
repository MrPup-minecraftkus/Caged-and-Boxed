package com.mrpup.caged_and_boxed.block;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.custom.BoxBlock;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;


public class ModBlocks {

    public static Block registerBlock(Block block, String name, boolean shouldRegisterItem) {
        ResourceLocation id = new ResourceLocation(CagedAndBoxed.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }


    private static BlockBehaviour.Properties cageProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 4.0f)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    public static final Block SMALL_CAGE = registerBlock(
            new CageBlock(CageSize.SMALL, cageProps()),
            "small_cage",
            true
    );

    public static final Block MEDIUM_CAGE = registerBlock(
            new CageBlock(CageSize.MEDIUM, cageProps()),
            "medium_cage",
            true
    );

    public static final Block UNIVERSAL_CAGE = registerBlock(
            new CageBlock(CageSize.UNIVERSAL, cageProps()),
            "universal_cage",
            true
    );

    public static final Block BOX_BLOCK = registerBlock(
            new BoxBlock(                    BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5f, 3.0f)
                    .sound(SoundType.WOOD)),
            "box_block",
            true
    );

    public static void register() {

    }
}
