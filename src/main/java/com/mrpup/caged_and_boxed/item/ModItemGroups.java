package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedAndBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class ModItemGroups {

    public static final CreativeModeTab CAGED = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(CagedAndBoxed.MOD_ID, "caged_and_boxed"),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(ModBlocks.SMALL_CAGE.asItem()))
                    .title(Component.translatable("itemgroup.caged_and_boxed.caged_and_boxed"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModBlocks.SMALL_CAGE.asItem());
                        entries.accept(ModBlocks.MEDIUM_CAGE.asItem());
                        entries.accept(ModBlocks.UNIVERSAL_CAGE.asItem());
                        entries.accept(ModBlocks.BOX_BLOCK.asItem());
                    }).build());

    public static void register() {

    }
}
