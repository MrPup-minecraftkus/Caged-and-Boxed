package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.item.custom.BoxBlockItem;
import com.mrpup.caged_and_boxed.item.custom.CageBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {

    public static Item registerItem(Item item, String id) {
        ResourceLocation itemID = ResourceLocation.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, id);
        Item registeredItem = Registry.register(BuiltInRegistries.ITEM, itemID, item);
        return registeredItem;
    }

    public static final Item SMALL_CAGE = registerItem(
            new CageBlockItem(ModBlocks.SMALL_CAGE, new Item.Properties().stacksTo(1)),
            "small_cage"
    );

    public static final Item MEDIUM_CAGE = registerItem(
            new CageBlockItem(ModBlocks.MEDIUM_CAGE, new Item.Properties().stacksTo(1)),
            "medium_cage"
    );

    public static final Item UNIVERSAL_CAGE = registerItem(
            new CageBlockItem(ModBlocks.UNIVERSAL_CAGE, new Item.Properties().stacksTo(1)),
            "universal_cage"
    );

    public static final Item BOX_BLOCK = registerItem(
            new BoxBlockItem(new Item.Properties().stacksTo(1)),
            "box_block"
    );

    public static void register() {

    }
}