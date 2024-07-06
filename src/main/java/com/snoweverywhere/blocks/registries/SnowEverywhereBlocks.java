package com.snoweverywhere.blocks.registries;

import com.snoweverywhere.blocks.SnowEverywhereBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class SnowEverywhereBlocks {
    public static final Block SNOW_EVERYWHERE_BLOCK = Registry.register(
        Registries.BLOCK,
        Identifier.of("snoweverywhere", "snow_everywhere_block"),
        new SnowEverywhereBlock(
            AbstractBlock.Settings.create()
            .mapColor(MapColor.WHITE)
            .replaceable()
            .strength(0.1f)
            .requiresTool()
            .sounds(BlockSoundGroup.SNOW)
            .nonOpaque()
            .dynamicBounds()
            .ticksRandomly()
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .pistonBehavior(PistonBehavior.DESTROY),
            () -> SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY
        )
    );

    public static void registerBlocks(){};
}