package com.mrpup.caged_and_boxed.block;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.custom.BoxBlock;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CagedandBoxed.MOD_ID);

    private static BlockBehaviour.Properties cageProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 4.0f)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    public static final DeferredBlock<CageBlock> SMALL_CAGE = BLOCKS.register(
            "small_cage", () -> new CageBlock(CageSize.SMALL, cageProps())
    );

    public static final DeferredBlock<CageBlock> MEDIUM_CAGE = BLOCKS.register(
            "medium_cage", () -> new CageBlock(CageSize.MEDIUM, cageProps())
    );

    public static final DeferredBlock<CageBlock> UNIVERSAL_CAGE = BLOCKS.register(
            "universal_cage", () -> new CageBlock(CageSize.UNIVERSAL, cageProps())
    );

    public static final DeferredBlock<BoxBlock> BOX_BLOCK = BLOCKS.register(
            "box_block", () -> new BoxBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(1.5f, 3.0f)
                            .sound(SoundType.WOOD)
            )
    );
}
