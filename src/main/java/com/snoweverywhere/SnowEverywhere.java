package com.snoweverywhere;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowEverywhere implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("snoweverywhere");
	public static final BooleanProperty FLAT = BooleanProperty.of("flat");

    public static final Block SNOW_EVERYWHERE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("snoweverywhere", "snow_everywhere_block"), Blocks.SNOW);

	public static final BlockEntityType<SnowEverywhereBlockEntity> SNOW_EVERYWHERE_BLOCK_ENTITY = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        new Identifier("snoweverywhere", "snow_everywhere_block_entity"),
        BlockEntityType.Builder.create(SnowEverywhereBlockEntity::new, SNOW_EVERYWHERE_BLOCK).build()
    );


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!");
	}
}