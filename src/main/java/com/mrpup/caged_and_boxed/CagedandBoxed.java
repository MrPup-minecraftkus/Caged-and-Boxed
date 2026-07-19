package com.mrpup.caged_and_boxed;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.block.enitities.ModBlockEntities;
import com.mrpup.caged_and_boxed.client.CageBlockEntityRenderer;
import com.mrpup.caged_and_boxed.item.ModItemGroups;
import com.mrpup.caged_and_boxed.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CagedandBoxed.MOD_ID)
public class CagedandBoxed {

    public static final String MOD_ID = "caged_and_boxed";


    public CagedandBoxed(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItemGroups.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    ModBlockEntities.CAGE_BLOCK_ENTITY.get(),
                    CageBlockEntityRenderer::new
            );
        }
    }
}
