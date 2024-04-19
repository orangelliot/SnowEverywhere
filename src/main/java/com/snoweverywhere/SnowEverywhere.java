package com.snoweverywhere;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowEverywhere implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	private static volatile World world;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
	
	public static void setWorld(World newWorld){
		world = newWorld;
	}

	public static WorldChunk getWorldChunk(BlockPos pos){
		return world.getWorldChunk(pos);
	}
}