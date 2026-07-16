package com.mrpup.caged_and_boxed;

import com.mrpup.caged_and_boxed.block.enitities.ModBlockEntities;
import com.mrpup.caged_and_boxed.client.CageBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = CagedandBoxed.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CagedandBoxed.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CagedandBoxedClient {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.CAGE_BLOCK_ENTITY.get(),
                CageBlockEntityRenderer::new
        );
    }
}
