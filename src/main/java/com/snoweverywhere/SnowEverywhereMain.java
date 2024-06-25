package com.snoweverywhere;

import com.snoweverywhere.blocks.registries.SEBlockEntities;
import com.snoweverywhere.blocks.registries.SEBlocks;
import com.snoweverywhere.blocks.registries.SEProperties;

import net.fabricmc.api.ModInitializer;

public class SnowEverywhereMain implements ModInitializer {
	public static final ModConfig CONFIG = ConfigLoader.loadConfig();

	@Override
	public void onInitialize() {
		//SEBlocks.registerBlocks();
		//SEBlockEntities.registerBlockEntities();
		SEProperties.registerProperties();
	}
}