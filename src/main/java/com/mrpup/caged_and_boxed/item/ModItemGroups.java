package com.mrpup.caged_and_boxed.item;

import com.mrpup.caged_and_boxed.CagedandBoxed;
import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItemGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CagedandBoxed.MOD_ID);

    public static final Supplier<CreativeModeTab> CAGED = CREATIVE_MODE_TAB.register("caged_and_boxed",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SMALL_CAGE.get()))
                    .title(Component.translatable("itemgroup.caged_and_boxed.caged_and_boxed"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.SMALL_CAGE.get());
                        output.accept(ModBlocks.MEDIUM_CAGE.get());
                        output.accept(ModBlocks.UNIVERSAL_CAGE.get());
                        output.accept(ModBlocks.BOX_BLOCK.get());
                    })
                    .build());

    public static void register(BusGroup eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
