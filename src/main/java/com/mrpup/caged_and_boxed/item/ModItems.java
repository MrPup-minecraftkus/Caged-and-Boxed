package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.item.custom.BoxBlockItem;
import com.mrpup.caged_and_boxed.item.custom.CageBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
    public static final Item SMALL_CAGE = register(
            "small_cage",
            settings -> new CageBlockItem(ModBlocks.SMALL_CAGE, settings),
            new Item.Properties().stacksTo(1)
    );

    public static final Item MEDIUM_CAGE = register(
            "medium_cage",
            settings -> new CageBlockItem(ModBlocks.MEDIUM_CAGE, settings),
            new Item.Properties().stacksTo(1)
    );

    public static final Item UNIVERSAL_CAGE = register(
            "universal_cage",
            settings -> new CageBlockItem(ModBlocks.UNIVERSAL_CAGE, settings),
            new Item.Properties().stacksTo(1)
    );

    public static final Item BOX_BLOCK = register(
            "box_block",
            BoxBlockItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static void register() {

    }
}