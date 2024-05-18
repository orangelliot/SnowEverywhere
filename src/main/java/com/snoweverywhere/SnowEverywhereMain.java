package com.snoweverywhere;

import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;
import com.snoweverywhere.blocks.registries.SnowEverywhereBlocks;

import net.fabricmc.api.ModInitializer;

public class SnowEverywhereMain implements ModInitializer {
	@Override
	public void onInitialize() {
		SnowEverywhereBlocks.registerBlocks();
		SnowEverywhereBlockEntities.registerBlockEntities();
	}
}