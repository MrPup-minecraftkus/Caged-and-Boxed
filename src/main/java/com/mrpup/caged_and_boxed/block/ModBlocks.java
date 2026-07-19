package com.mrpup.caged_and_boxed.block;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.custom.BoxBlock;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;


public class ModBlocks {

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, Block.Properties settings, boolean shouldRegisterItem) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        return block;
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, name));
    }

    private static Block.Properties cageProps() {
        return Block.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(2.0f, 4.0f)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    public static final Block SMALL_CAGE = register(
            "small_cage",
            settings -> new CageBlock(CageSize.SMALL, settings),
            cageProps(),
            true
    );

    public static final Block MEDIUM_CAGE = register(
            "medium_cage",
            settings -> new CageBlock(CageSize.MEDIUM, settings),
            cageProps(),
            true
    );

    public static final Block UNIVERSAL_CAGE = register(
            "universal_cage",
            settings -> new CageBlock(CageSize.UNIVERSAL, settings),
            cageProps(),
            true
    );

    public static final Block BOX_BLOCK = register(
            "box_block",
            BoxBlock::new,
            Block.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(1.5f, 3.0f)
                    .sound(SoundType.WOOD),
            true
    );

    public static void register() {

    }
}
