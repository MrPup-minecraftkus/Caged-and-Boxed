package com.mrpup.caged_and_boxed;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.block.enitities.ModBlockEntities;
import com.mrpup.caged_and_boxed.item.ModItemGroups;
import com.mrpup.caged_and_boxed.item.ModItems;
import net.fabricmc.api.ModInitializer;


public class CagedAndBoxed implements ModInitializer {
	public static final String MOD_ID = "caged_and_boxed";

	@Override
	public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModItemGroups.register();
	}
}