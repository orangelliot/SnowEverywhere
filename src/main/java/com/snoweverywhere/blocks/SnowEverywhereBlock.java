package com.snoweverywhere.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;
import com.snoweverywhere.blocks.entities.SnowEverywhereBlockEntity;
import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnowEverywhereBlock extends BlockWithEntity{
    public static final MapCodec<SnowEverywhereBlock> CODEC = SnowEverywhereBlock.createCodec(settings -> new SnowEverywhereBlock((AbstractBlock.Settings)settings, () -> SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY));
    public static final IntProperty LAYERS = Properties.LAYERS;
    public static final int MAX_LAYERS = 8;
    public NbtCompound nbt;

    public SnowEverywhereBlock(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends SnowEverywhereBlockEntity>> supplier) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
        nbt = new NbtCompound();
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
            int i = blockState.get(SnowBlock.LAYERS);
            return (BlockState)blockState.with(SnowBlock.LAYERS, Math.min(8, i + 1));
        }
        return this.getDefaultState();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        if(oldState.isOf(state.getBlock()) || world.isClient)
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof SnowEverywhereBlockEntity){
            nbt.putBoolean("notify", true);
            blockEntity.readNbt(nbt);
            blockEntity.markDirty();
        }
    }

    @Override
    public MapCodec<? extends SnowEverywhereBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}