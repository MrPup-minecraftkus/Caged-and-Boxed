package com.mrpup.caged_and_boxed.client;

import com.mrpup.caged_and_boxed.block.enitities.ModBlockEntities;
import com.mrpup.caged_and_boxed.client.renderer.CageBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class CagedAndBoxedClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        BlockEntityRenderers.register(
                ModBlockEntities.CAGE_BLOCK_ENTITY,
                CageBlockEntityRenderer::new
        );
	}
}