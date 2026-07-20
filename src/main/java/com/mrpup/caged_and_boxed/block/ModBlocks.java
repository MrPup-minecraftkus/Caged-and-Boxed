package com.mrpup.caged_and_boxed.block;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.custom.BoxBlock;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CagedandBoxed.MOD_ID);

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(CagedandBoxed.MOD_ID, name));
    }

    private static BlockBehaviour.Properties cageProps(String name) {
        return BlockBehaviour.Properties.of()
                .setId(blockKey(name))
                .mapColor(MapColor.METAL)
                .strength(2.0f, 4.0f)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    public static final RegistryObject<CageBlock> SMALL_CAGE = BLOCKS.register(
            "small_cage", () -> new CageBlock(CageSize.SMALL, cageProps("small_cage"))
    );

    public static final RegistryObject<CageBlock> MEDIUM_CAGE = BLOCKS.register(
            "medium_cage", () -> new CageBlock(CageSize.MEDIUM, cageProps("medium_cage"))
    );

    public static final RegistryObject<CageBlock> UNIVERSAL_CAGE = BLOCKS.register(
            "universal_cage", () -> new CageBlock(CageSize.UNIVERSAL, cageProps("universal_cage"))
    );

    public static final RegistryObject<BoxBlock> BOX_BLOCK = BLOCKS.register(
            "box_block", () -> new BoxBlock(
                    BlockBehaviour.Properties.of()
                            .setId(blockKey("box_block"))
                            .mapColor(MapColor.WOOD)
                            .strength(1.5f, 3.0f)
                            .sound(SoundType.WOOD)
            )
    );
}
