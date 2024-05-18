package com.snoweverywhere.blocks.registries;


import com.snoweverywhere.blocks.entities.SnowEverywhereBlockEntity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SnowEverywhereBlockEntities {
    public static final BlockEntityType<SnowEverywhereBlockEntity> SNOW_EVERYWHERE_BLOCK_ENTITY = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        new Identifier("snoweverywhere", "snow_everywhere_block_entity"),
        BlockEntityType.Builder.create(SnowEverywhereBlockEntity::new, SnowEverywhereBlocks.SNOW_EVERYWHERE_BLOCK).build()
    );

    public static void registerBlockEntities(){};
}