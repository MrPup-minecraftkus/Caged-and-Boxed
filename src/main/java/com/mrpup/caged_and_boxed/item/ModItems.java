package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.item.custom.BoxBlockItem;
import com.mrpup.caged_and_boxed.item.custom.CageBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CagedandBoxed.MOD_ID);

    public static final RegistryObject<CageBlockItem> SMALL_CAGE = ITEMS.register(
            "small_cage", () -> new CageBlockItem(ModBlocks.SMALL_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<CageBlockItem> MEDIUM_CAGE = ITEMS.register(
            "medium_cage", () -> new CageBlockItem(ModBlocks.MEDIUM_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<CageBlockItem> UNIVERSAL_CAGE = ITEMS.register(
            "universal_cage", () -> new CageBlockItem(ModBlocks.UNIVERSAL_CAGE.get(), new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<BoxBlockItem> BOX_BLOCK = ITEMS.register(
            "box_block", () -> new BoxBlockItem(new Item.Properties().stacksTo(1))
    );
}