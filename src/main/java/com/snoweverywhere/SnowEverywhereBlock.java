package com.snoweverywhere;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SnowEverywhereBlock extends SnowBlock implements BlockEntityProvider{
    public static final Identifier ID = new Identifier("snoweverywhere", "snow_everywhere_block");
    // This is your block entity type which you'll register later
    public static BlockEntityType<SnowEverywhereBlockEntity> BLOCK_ENTITY;

    public SnowEverywhereBlock (AbstractBlock.Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SnowEverywhereBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            int i = blockState.get(LAYERS);
            return (BlockState)blockState.with(LAYERS, Math.min(8, i + 1));
        }
        return this.getDefaultState();
    }
}