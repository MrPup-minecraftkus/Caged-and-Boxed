package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.item.custom.BoxBlockItem;
import com.mrpup.caged_and_boxed.item.custom.CageBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CagedandBoxed.MOD_ID);

    public static final DeferredItem<CageBlockItem> SMALL_CAGE = ITEMS.register(
            "small_cage", () -> new CageBlockItem(ModBlocks.SMALL_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<CageBlockItem> MEDIUM_CAGE = ITEMS.register(
            "medium_cage", () -> new CageBlockItem(ModBlocks.MEDIUM_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<CageBlockItem> UNIVERSAL_CAGE = ITEMS.register(
            "universal_cage", () -> new CageBlockItem(ModBlocks.UNIVERSAL_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<BoxBlockItem> BOX_BLOCK = ITEMS.register(
            "box_block", () -> new BoxBlockItem(new Item.Properties().stacksTo(1))
    );
}